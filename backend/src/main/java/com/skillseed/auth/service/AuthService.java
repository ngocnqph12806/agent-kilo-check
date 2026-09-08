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
import com.skillseed.auth.security.RefreshTokenCookie;
import com.skillseed.notification.EmailTemplateService;
import com.skillseed.shared.domain.AuthProvider;
import com.skillseed.shared.security.InMemoryRateLimiter;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
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

    /** FR-M07: throttle forgot-password requests to discourage email bombing. */
    static final Duration FORGOT_PASSWORD_WINDOW = Duration.ofHours(1);
    static final int FORGOT_PASSWORD_MAX_ATTEMPTS = 5;

    /** FR-M06: throttle verify-email attempts per client to block token spray. */
    static final Duration VERIFY_EMAIL_WINDOW = Duration.ofMinutes(15);
    static final int VERIFY_EMAIL_MAX_ATTEMPTS = 10;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenStore tokenStore;
    private final EmailTemplateService emailTemplateService;
    private final RateLimiter rateLimiter;
    private final InMemoryRateLimiter inMemoryRateLimiter;
    private final Map<String, OAuthIdTokenVerifier> oauthVerifiers;
    private final String publicBaseUrl;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        TokenStore tokenStore,
        EmailTemplateService emailTemplateService,
        RateLimiter rateLimiter,
        InMemoryRateLimiter inMemoryRateLimiter,
        ObjectProvider<List<OAuthIdTokenVerifier>> oauthProvider,
        @Value("${app.public-base-url:http://localhost:3000}") String publicBaseUrl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
        this.emailTemplateService = emailTemplateService;
        this.rateLimiter = rateLimiter;
        this.inMemoryRateLimiter = inMemoryRateLimiter;
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
     * @return the freshly created {@link User} so the caller can build a
     *         {@code RegisterResponse} summary.
     * @throws AuthException with code EMAIL_ALREADY_EXISTS if the email
     *                       is already registered
     */
    @Transactional
    public User register(RegisterRequest req) {
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
        if (req.phone() != null && !req.phone().isBlank()) {
            user.setPhone(req.phone().trim());
        }
        if (req.countryCode() != null && !req.countryCode().isBlank()) {
            user.setCountryCode(req.countryCode().trim().toUpperCase(Locale.ROOT));
        }
        if (req.timezone() != null && !req.timezone().isBlank()) {
            user.setTimezone(req.timezone().trim());
        }
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);

        sendVerificationEmail(user);
        log.info("Registered new user id={} email={} (country={}, tz={}, invite={})",
                id, email,
                user.getCountryCode(),
                user.getTimezone(),
                req.inviteCode() != null ? "yes" : "no");
        return user;
    }

    /**
     * Verifies the email address associated with the supplied token. Marks
     * the user as verified and bumps {@code verification_level} to 0
     * (email-level).
     *
     * @throws AuthException with code INVALID_TOKEN or TOKEN_EXPIRED if
     *                       the token is unknown, already consumed, or expired.
     */
    @Transactional
    public void verifyEmail(String token) {
        verifyEmail(token, "unknown");
    }

    /**
     * Verify-email overload that additionally accepts a stable client key
     * (typically the originating IP) so the call can be rate-limited
     * against token-spraying and email-bombing (FR-M06). The previous
     * single-arg form remains for callers that don't have a key yet.
     */
    @Transactional
    public void verifyEmail(String token, String clientKey) {
        inMemoryRateLimiter.acquireOrThrow(
                "verify-email:" + clientKey,
                VERIFY_EMAIL_MAX_ATTEMPTS,
                VERIFY_EMAIL_WINDOW,
                "RATE_LIMIT_VERIFY_EMAIL");
        Optional<String> payload = tokenStore.consume(PURPOSE_EMAIL_VERIFY, token);
        if (payload.isEmpty()) {
            throw AuthException.badRequest("INVALID_TOKEN",
                "Verification token is invalid or expired");
        }
        UUID userId = UUID.fromString(payload.get());
        User user = userRepository.findById(userId)
            .orElseThrow(() -> AuthException.badRequest("USER_NOT_FOUND",
                "User no longer exists"));
        boolean wasUnverified = !user.isVerified();
        user.setVerified(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("Verified email for user id={}", userId);
        if (wasUnverified) {
            emailTemplateService.sendWelcomeEmail(user);
        }
    }

    /**
     * Authenticates an email+password user. Enforces a per-IP rate limit
     * (5 attempts / 15 minutes by default — see {@link #LOGIN_WINDOW} and
     * {@link #LOGIN_MAX_ATTEMPTS}). On success, returns a fresh access +
     * refresh token pair and stores the refresh token in Redis so it can
     * be revoked on logout. The cookie flag is computed from the request
     * scheme (HTTPS in production).
     *
     * @throws AuthException with one of {@code RATE_LIMITED},
     *                       {@code INVALID_CREDENTIALS}, or {@code OAUTH_ONLY_ACCOUNT}.
     */
    public AuthTokenResponse login(LoginRequest req, String clientKey) {
        return login(req, clientKey, false);
    }

    public AuthTokenResponse login(LoginRequest req, String clientKey, boolean secureCookie) {
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
        return issueTokens(user, secureCookie);
    }

    /**
     * Validates a refresh token (signature + Redis presence) and issues a
     * new access token. The refresh token itself is rotated: the old value
     * is consumed and a new refresh token is issued.
     *
     * <p>Token source precedence: {@code HttpOnly} cookie
     * ({@link RefreshTokenCookie#NAME}) first, then request body. This
     * lets hardened clients omit the token from the JSON body.
     *
     * @throws AuthException with code {@code INVALID_TOKEN} when the
     *                       refresh token is unknown, expired, or already used.
     */
    public AuthTokenResponse refresh(RefreshTokenRequest req) {
        return refresh(null, req, false);
    }

    public AuthTokenResponse refresh(HttpServletRequest httpRequest,
                                     RefreshTokenRequest req,
                                     boolean secureCookie) {
        String cookieToken = RefreshTokenCookie.read(httpRequest);
        String token = cookieToken != null
                ? cookieToken
                : (req == null ? null : req.refreshToken());
        if (token == null || token.isBlank()) {
            throw AuthException.unauthorized("INVALID_TOKEN",
                "Refresh token is required (cookie or body)");
        }
        Claims claims;
        try {
            claims = jwtService.parseAndValidate(token, "refresh");
        } catch (Exception ex) {
            throw AuthException.unauthorized("INVALID_TOKEN", "Refresh token is invalid");
        }
        UUID userId = UUID.fromString(claims.getSubject());

        Optional<String> stored = tokenStore.consume(PURPOSE_REFRESH_TOKEN, token);
        if (stored.isEmpty() || !userId.toString().equals(stored.get())) {
            throw AuthException.unauthorized("INVALID_TOKEN",
                "Refresh token is invalid or expired");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> AuthException.unauthorized("USER_NOT_FOUND",
                "User no longer exists"));
        return issueTokens(user, secureCookie);
    }

    /**
     * Revokes the supplied refresh token (cookie or body) and clears
     * the cookie. Idempotent: revoking an unknown token is a no-op.
     */
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        tokenStore.delete(PURPOSE_REFRESH_TOKEN, refreshToken);
    }

    public void logout(HttpServletRequest httpRequest,
                       RefreshTokenRequest req) {
        String cookieToken = RefreshTokenCookie.read(httpRequest);
        String token = cookieToken != null
                ? cookieToken
                : (req == null ? null : req.refreshToken());
        if (token == null || token.isBlank()) {
            return;
        }
        tokenStore.delete(PURPOSE_REFRESH_TOKEN, token);
    }

    /**
     * Always returns a generic "If the email exists..." message to avoid
     * leaking account existence. If the account is an email+password user
     * and exists, a single-use reset token is generated (TTL 1h) and a
     * reset email is queued.
     */
    public void forgotPassword(ForgotPasswordRequest req) {
        forgotPassword(req, "unknown");
    }

    public void forgotPassword(ForgotPasswordRequest req, String clientKey) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        // Throttle BEFORE the user lookup so an attacker can't probe
        // account existence via timing, and so email-bombing a real
        // account is capped (FR-M07).
        inMemoryRateLimiter.acquireOrThrow(
                "forgot-password:" + email,
                FORGOT_PASSWORD_MAX_ATTEMPTS,
                FORGOT_PASSWORD_WINDOW,
                "RATE_LIMIT_FORGOT_PASSWORD");
        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isEmpty() || maybeUser.get().getPasswordHash() == null) {
            log.info("forgotPassword: no actionable account for email");
            return;
        }
        User user = maybeUser.get();
        String token = TokenGenerator.generate();
        tokenStore.store(PURPOSE_PASSWORD_RESET, token, user.getId().toString(),
            Duration.ofHours(1));
        emailTemplateService.sendPasswordResetEmail(user, token);
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
        return loginWithGoogle(req, false);
    }

    public AuthTokenResponse loginWithGoogle(OAuthGoogleRequest req, boolean secureCookie) {
        return oauthLogin("google", req.idToken(), null, secureCookie);
    }

    /**
     * Verifies an Apple {@code id_token} and signs the corresponding user
     * in. {@code fullName} is only available from Apple during the first
     * sign-in and is applied to newly created accounts.
     */
    @Transactional
    public AuthTokenResponse loginWithApple(OAuthAppleRequest req, boolean secureCookie) {
        return oauthLogin("apple", req.idToken(), req.fullName(), secureCookie);
    }

    private AuthTokenResponse oauthLogin(String provider, String idToken,
                                         String fallbackName, boolean secureCookie) {
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
        return issueTokens(user, secureCookie);
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
        // Persistence happens in oauthLogin so the call site issues exactly
        // one save() per login (regardless of existing vs new account).
        log.info("Created OAuth user id={} provider={}", user.getId(), provider);
        return user;
    }

    private AuthException invalidCredentials() {
        return AuthException.unauthorized("INVALID_CREDENTIALS", "Invalid email or password");
    }

    AuthTokenResponse issueTokens(User user, boolean secureCookie) {
        String access = jwtService.generateAccessToken(
            user.getId(), user.getEmail(), user.getVerificationLevel(), user.getRole());
        String refresh = jwtService.generateRefreshToken(user.getId());
        tokenStore.store(PURPOSE_REFRESH_TOKEN, refresh, user.getId().toString(),
            Duration.ofSeconds(jwtService.getRefreshTtlSeconds()));
        return new AuthTokenResponse(
            access,
            refresh,
            jwtService.getAccessTtlSeconds(),
            "Bearer",
            toSummary(user),
            secureCookie);
    }

    public UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getAvatarUrl(),
            user.isVerified(),
            user.getVerificationLevel(),
            user.isOnboardingCompleted(),
            user.getAuthProvider().name().toLowerCase(Locale.ROOT),
            user.getCountryCode(),
            user.getTimezone());
    }

    private void sendVerificationEmail(User user) {
        String token = TokenGenerator.generate();
        tokenStore.store(PURPOSE_EMAIL_VERIFY, token, user.getId().toString(),
            TokenGenerator.defaultTtl());
        emailTemplateService.sendVerificationEmail(user, token);
    }
}
