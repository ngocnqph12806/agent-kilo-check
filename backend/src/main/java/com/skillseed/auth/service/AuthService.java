package com.skillseed.auth.service;

import com.skillseed.auth.dto.AuthTokenResponse;
import com.skillseed.auth.dto.ForgotPasswordRequest;
import com.skillseed.auth.dto.LoginRequest;
import com.skillseed.auth.dto.OAuthAppleRequest;
import com.skillseed.auth.dto.OAuthGoogleRequest;
import com.skillseed.auth.dto.RefreshTokenRequest;
import com.skillseed.auth.dto.RegisterRequest;
import com.skillseed.auth.dto.ResetPasswordRequest;
import com.skillseed.auth.dto.UserSummaryResponse;
import com.skillseed.auth.exception.AuthException;
import com.skillseed.notification.EmailSender;
import com.skillseed.shared.domain.AuthProvider;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * High-level auth operations: register, email verification, login, refresh,
 * forgot/reset password, and OAuth linking.
 *
 * <p>Persists {@link User} entities and orchestrates the token store,
 * email sender, and {@link JwtService}.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    static final String PURPOSE_EMAIL_VERIFY = "email-verify";
    static final String PURPOSE_PASSWORD_RESET = "password-reset";
    static final String PURPOSE_REFRESH_TOKEN = "refresh-token";

    static final Duration LOGIN_WINDOW = Duration.ofMinutes(15);
    static final int LOGIN_MAX_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenStore tokenStore;
    private final EmailSender emailSender;
    private final RateLimiter rateLimiter;
    private final Map<String, OAuthIdTokenVerifier> oauthVerifiers;
    private final String publicBaseUrl;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TokenStore tokenStore,
            EmailSender emailSender,
            RateLimiter rateLimiter,
            ObjectProvider<List<OAuthIdTokenVerifier>> oauthProvider,
            @Value("${app.public-base-url:http://localhost:3000}") String publicBaseUrl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
        this.emailSender = emailSender;
        this.rateLimiter = rateLimiter;
        this.oauthVerifiers = oauthProvider.getIfAvailable(List::of).stream()
                .collect(Collectors.toMap(
                        v -> v.configSummary().get("provider"),
                        v -> v));
        this.publicBaseUrl = publicBaseUrl;
    }

    /**
     * Registers a new email+password user, stores a hashed password, and
     * issues an email verification token (TTL 24h).
     *
     * @throws AuthException with code EMAIL_ALREADY_EXISTS if the email
     *     is already registered
     */
    @Transactional
    public void register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw AuthException.conflict("EMAIL_ALREADY_EXISTS",
                    "Email is already registered");
        }
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        User user = new User(id, email, req.fullName().trim());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setAuthProvider(AuthProvider.EMAIL);
        user.setVerified(false);
        user.setVerificationLevel((short) 0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);

        sendVerificationEmail(user);
        log.info("Registered new user id={} email={}", id, email);
    }

    /**
     * Verifies the email address associated with the supplied token. Marks
     * the user as verified and bumps {@code verification_level} to 0
     * (email-level).
     *
     * @throws AuthException with code INVALID_TOKEN or TOKEN_EXPIRED if
     *     the token is unknown, already consumed, or expired.
     */
    @Transactional
    public void verifyEmail(String token) {
        Optional<String> payload = tokenStore.consume(PURPOSE_EMAIL_VERIFY, token);
        if (payload.isEmpty()) {
            throw AuthException.badRequest("INVALID_TOKEN",
                    "Verification token is invalid or expired");
        }
        UUID userId = UUID.fromString(payload.get());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.badRequest("USER_NOT_FOUND",
                        "User no longer exists"));
        user.setVerified(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("Verified email for user id={}", userId);
    }

    /**
     * Authenticates an email+password user. Enforces a per-IP rate limit
     * (5 attempts / 15 minutes by default — see {@link #LOGIN_WINDOW} and
     * {@link #LOGIN_MAX_ATTEMPTS}). On success, returns a fresh access +
     * refresh token pair and stores the refresh token in Redis so it can
     * be revoked on logout.
     *
     * @throws AuthException with one of {@code RATE_LIMITED},
     *     {@code INVALID_CREDENTIALS}, or {@code OAUTH_ONLY_ACCOUNT}.
     */
    public AuthTokenResponse login(LoginRequest req, String clientKey) {
        String rateKey = clientKey == null || clientKey.isBlank() ? "unknown" : clientKey;
        if (!rateLimiter.tryAcquire("login", rateKey, LOGIN_MAX_ATTEMPTS, LOGIN_WINDOW)) {
            long retry = rateLimiter.retryAfterSeconds("login", rateKey,
                    LOGIN_MAX_ATTEMPTS, LOGIN_WINDOW);
            throw AuthException.tooManyRequests("RATE_LIMITED",
                    "Too many login attempts; retry in " + retry + "s");
        }

        String email = req.email().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(this::invalidCredentials);

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            log.warn("Login attempt on passwordless account id={}", user.getId());
            throw AuthException.unauthorized("OAUTH_ONLY_ACCOUNT",
                    "This account uses social sign-in");
        }
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }
        log.info("User login id={}", user.getId());
        return issueTokens(user);
    }

    /**
     * Validates a refresh token (signature + Redis presence) and issues a
     * new access token. The refresh token itself is rotated: the old value
     * is consumed and a new refresh token is issued.
     *
     * @throws AuthException with code {@code INVALID_TOKEN} when the
     *     refresh token is unknown, expired, or already used.
     */
    public AuthTokenResponse refresh(RefreshTokenRequest req) {
        Claims claims;
        try {
            claims = jwtService.parseAndValidate(req.refreshToken(), "refresh");
        } catch (Exception ex) {
            throw AuthException.unauthorized("INVALID_TOKEN", "Refresh token is invalid");
        }
        UUID userId = UUID.fromString(claims.getSubject());

        Optional<String> stored = tokenStore.consume(PURPOSE_REFRESH_TOKEN, req.refreshToken());
        if (stored.isEmpty() || !userId.toString().equals(stored.get())) {
            throw AuthException.unauthorized("INVALID_TOKEN",
                    "Refresh token is invalid or expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.unauthorized("USER_NOT_FOUND",
                        "User no longer exists"));
        return issueTokens(user);
    }

    /**
     * Revokes the supplied refresh token. Idempotent: revoking an unknown
     * token is a no-op.
     */
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        tokenStore.delete(PURPOSE_REFRESH_TOKEN, refreshToken);
    }

    /**
     * Always returns a generic "If the email exists..." message to avoid
     * leaking account existence. If the account is an email+password user
     * and exists, a single-use reset token is generated (TTL 1h) and a
     * reset email is queued.
     */
    public void forgotPassword(ForgotPasswordRequest req) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isEmpty() || maybeUser.get().getPasswordHash() == null) {
            log.info("forgotPassword: no actionable account for email");
            return;
        }
        User user = maybeUser.get();
        String token = TokenGenerator.generate();
        tokenStore.store(PURPOSE_PASSWORD_RESET, token, user.getId().toString(),
                Duration.ofHours(1));
        String link = publicBaseUrl + "/reset-password/" + token;
        String subject = "Reset your SkillSeed password";
        String text = "We received a request to reset your SkillSeed password.\n\n"
                + "Open the link below to choose a new password (expires in 1 hour):\n"
                + link + "\n\n"
                + "If you did not request this, you can safely ignore this email.";
        String html = "<p>We received a request to reset your SkillSeed password.</p>"
                + "<p>Open the link below to choose a new password (expires in 1 hour):</p>"
                + "<p><a href=\"" + link + "\">Reset password</a></p>"
                + "<p>If you did not request this, you can safely ignore this email.</p>";
        try {
            emailSender.send(user.getEmail(), subject, html, text);
        } catch (Exception ex) {
            log.warn("Failed to send reset email to {}: {}", user.getEmail(), ex.getMessage());
        }
    }

    /**
     * Consumes a reset token and updates the user's password hash. The
     * token is single-use; subsequent calls with the same token fail with
     * {@code INVALID_TOKEN}.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        Optional<String> payload = tokenStore.consume(PURPOSE_PASSWORD_RESET, req.token());
        if (payload.isEmpty()) {
            throw AuthException.badRequest("INVALID_TOKEN",
                    "Reset token is invalid or expired");
        }
        UUID userId = UUID.fromString(payload.get());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.badRequest("USER_NOT_FOUND",
                        "User no longer exists"));
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw AuthException.badRequest("OAUTH_ONLY_ACCOUNT",
                    "This account uses social sign-in");
        }
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("Password reset for user id={}", userId);
    }

    /**
     * Verifies a Google {@code id_token} and signs the corresponding user
     * in. If no user with the same email exists a new one is created
     * (auth_provider = GOOGLE, verified = true since Google has verified
     * the email).
     */
    @Transactional
    public AuthTokenResponse loginWithGoogle(OAuthGoogleRequest req) {
        return oauthLogin("google", req.idToken(), null);
    }

    /**
     * Verifies an Apple {@code id_token} and signs the corresponding user
     * in. {@code fullName} is only available from Apple during the first
     * sign-in and is applied to newly created accounts.
     */
    @Transactional
    public AuthTokenResponse loginWithApple(OAuthAppleRequest req) {
        return oauthLogin("apple", req.idToken(), req.fullName());
    }

    private AuthTokenResponse oauthLogin(String provider, String idToken, String fallbackName) {
        OAuthIdTokenVerifier verifier = oauthVerifiers.get(provider);
        if (verifier == null) {
            throw AuthException.badRequest("OAUTH_PROVIDER_DISABLED",
                    provider + " sign-in is not configured");
        }
        OAuthIdTokenVerifier.VerifiedProfile profile = verifier.verify(idToken);
        Optional<User> existing = userRepository.findByEmail(profile.email());
        User user = existing.orElseGet(() -> createOAuthUser(provider, profile, fallbackName));
        if (user.getAuthProvider() == AuthProvider.EMAIL) {
            user.setAuthProvider(AuthProvider.valueOf(provider.toUpperCase(Locale.ROOT)));
        }
        if (!user.isVerified()) {
            user.setVerified(true);
        }
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return issueTokens(user);
    }

    private User createOAuthUser(String provider,
            OAuthIdTokenVerifier.VerifiedProfile profile, String fallbackName) {
        if (profile.email() == null || profile.email().isBlank()) {
            throw AuthException.badRequest("EMAIL_REQUIRED",
                    provider + " did not return a verifiable email");
        }
        User user = new User(UUID.randomUUID(), profile.email().toLowerCase(Locale.ROOT),
                profile.name() != null && !profile.name().isBlank() ? profile.name()
                        : (fallbackName != null && !fallbackName.isBlank()
                                ? fallbackName
                                : profile.email().split("@")[0]));
        user.setAuthProvider(AuthProvider.valueOf(provider.toUpperCase(Locale.ROOT)));
        user.setVerified(true);
        user.setVerificationLevel((short) 0);
        user.setPasswordHash(null);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("Created OAuth user id={} provider={}", user.getId(), provider);
        return user;
    }

    private AuthException invalidCredentials() {
        return AuthException.unauthorized("INVALID_CREDENTIALS", "Invalid email or password");
    }

    AuthTokenResponse issueTokens(User user) {
        String access = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getVerificationLevel());
        String refresh = jwtService.generateRefreshToken(user.getId());
        tokenStore.store(PURPOSE_REFRESH_TOKEN, refresh, user.getId().toString(),
                Duration.ofSeconds(jwtService.getRefreshTtlSeconds()));
        return new AuthTokenResponse(
                access,
                refresh,
                jwtService.getAccessTtlSeconds(),
                "Bearer",
                toSummary(user));
    }

    UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isVerified(),
                user.getVerificationLevel(),
                user.isOnboardingCompleted(),
                user.getAuthProvider().name().toLowerCase(Locale.ROOT));
    }

    private void sendVerificationEmail(User user) {
        String token = TokenGenerator.generate();
        tokenStore.store(PURPOSE_EMAIL_VERIFY, token, user.getId().toString(),
                TokenGenerator.defaultTtl());
        String link = publicBaseUrl + "/verify-email/" + token;
        String subject = "Verify your SkillSeed email";
        String text = "Welcome to SkillSeed!\n\n"
                + "Confirm your email by opening: " + link + "\n\n"
                + "This link expires in 24 hours.";
        String html = "<p>Welcome to SkillSeed!</p>"
                + "<p>Confirm your email by clicking the link below (expires in 24h):</p>"
                + "<p><a href=\"" + link + "\">Verify email</a></p>";
        try {
            emailSender.send(user.getEmail(), subject, html, text);
        } catch (Exception ex) {
            log.warn("Failed to send verification email to {}: {}", user.getEmail(),
                    ex.getMessage());
        }
    }
}