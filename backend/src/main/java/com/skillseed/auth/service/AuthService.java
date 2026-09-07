package com.skillseed.auth.service;

import com.skillseed.auth.dto.AuthTokenResponse;
import com.skillseed.auth.dto.RegisterRequest;
import com.skillseed.auth.dto.UserSummaryResponse;
import com.skillseed.auth.exception.AuthException;
import com.skillseed.notification.EmailSender;
import com.skillseed.shared.domain.AuthProvider;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenStore tokenStore;
    private final EmailSender emailSender;
    private final String publicBaseUrl;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TokenStore tokenStore,
            EmailSender emailSender,
            @Value("${app.public-base-url:http://localhost:3000}") String publicBaseUrl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
        this.emailSender = emailSender;
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

    AuthTokenResponse issueTokens(User user) {
        String access = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getVerificationLevel());
        String refresh = jwtService.generateRefreshToken(user.getId());
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