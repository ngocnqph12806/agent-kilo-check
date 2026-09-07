package com.skillseed.auth.service;

import com.skillseed.auth.dto.AuthTokenResponse;
import com.skillseed.auth.dto.ForgotPasswordRequest;
import com.skillseed.auth.dto.LoginRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthService}. Mocks the user repository, password
 * encoder, JWT/token-store collaborators, email sender, rate limiter, and
 * the OAuth verifier list. Exercises every public branch: success,
 * conflict, rate-limited, invalid credentials, OAuth-only, token expiry,
 * forgot/reset password, OAuth linking, and token rotation.
 */
class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private TokenStore tokenStore;
    private EmailSender emailSender;
    private RateLimiter rateLimiter;
    private AuthService authService;
    private OAuthIdTokenVerifier googleVerifier;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        tokenStore = mock(TokenStore.class);
        emailSender = mock(EmailSender.class);
        rateLimiter = mock(RateLimiter.class);

        googleVerifier = mock(OAuthIdTokenVerifier.class);
        when(googleVerifier.configSummary()).thenReturn(Map.of("provider", "google"));

        @SuppressWarnings("unchecked")
        ObjectProvider<List<OAuthIdTokenVerifier>> provider = mock(ObjectProvider.class);
        List<OAuthIdTokenVerifier> verifiers = List.of(googleVerifier);
        lenient().when(provider.getIfAvailable(any(Supplier.class))).thenAnswer(inv -> verifiers);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                tokenStore,
                emailSender,
                rateLimiter,
                provider,
                "https://app.skillseed.test");
    }

    private static User existingEmailUser(UUID id) {
        User user = new User(id, "alice@example.com", "Alice");
        user.setPasswordHash("$2a$12$hash");
        user.setAuthProvider(AuthProvider.EMAIL);
        user.setVerified(true);
        return user;
    }

    @Nested
    class Register {

        @Test
        void createsUserSendsEmailAndStoresVerifyToken() {
            when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
            when(passwordEncoder.encode("Password1")).thenReturn("$2a$12$encoded");

            authService.register(new RegisterRequest("alice@example.com", "Password1", "Alice"));

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();
            assertThat(saved.getEmail()).isEqualTo("alice@example.com");
            assertThat(saved.getPasswordHash()).isEqualTo("$2a$12$encoded");
            assertThat(saved.getAuthProvider()).isEqualTo(AuthProvider.EMAIL);
            assertThat(saved.isVerified()).isFalse();

            verify(tokenStore).store(eq(AuthService.PURPOSE_EMAIL_VERIFY), anyString(),
                    eq(saved.getId().toString()), eq(Duration.ofHours(24)));
            verify(emailSender).send(eq("alice@example.com"), anyString(),
                    anyString(), anyString());
        }

        @Test
        void duplicateEmailThrows409() {
            when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(
                    new RegisterRequest("alice@example.com", "Password1", "Alice")))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> {
                        AuthException ae = (AuthException) ex;
                        assertThat(ae.getCode()).isEqualTo("EMAIL_ALREADY_EXISTS");
                        assertThat(ae.getHttpStatus()).isEqualTo(409);
                    });

            verify(userRepository, never()).save(any());
        }

        @Test
        void emailIsNormalisedLowercaseAndTrimmed() {
            when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);

            authService.register(new RegisterRequest("  Bob@Example.COM ", "Password1", "Bob"));

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo("bob@example.com");
        }
    }

    @Nested
    class VerifyEmail {

        @Test
        void flipsVerifiedOnValidToken() {
            UUID id = UUID.randomUUID();
            when(tokenStore.consume(AuthService.PURPOSE_EMAIL_VERIFY, "tok")).thenReturn(
                    Optional.of(id.toString()));
            when(userRepository.findById(id)).thenReturn(Optional.of(existingEmailUser(id)));

            authService.verifyEmail("tok");

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().isVerified()).isTrue();
        }

        @Test
        void rejectsUnknownToken() {
            when(tokenStore.consume(anyString(), anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.verifyEmail("expired"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("INVALID_TOKEN"));
        }

        @Test
        void rejectsTokenForDeletedUser() {
            UUID id = UUID.randomUUID();
            when(tokenStore.consume(anyString(), anyString()))
                    .thenReturn(Optional.of(id.toString()));
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.verifyEmail("tok"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("USER_NOT_FOUND"));
        }
    }

    @Nested
    class Login {

        @Test
        void returnsTokensOnValidCredentials() {
            UUID id = UUID.randomUUID();
            when(rateLimiter.tryAcquire(eq("login"), anyString(), anyInt(), any()))
                    .thenReturn(true);
            when(userRepository.findByEmail("alice@example.com"))
                    .thenReturn(Optional.of(existingEmailUser(id)));
            when(passwordEncoder.matches("Password1", "$2a$12$hash")).thenReturn(true);
            when(jwtService.generateAccessToken(eq(id), anyString(), any())).thenReturn("access");
            when(jwtService.generateRefreshToken(id)).thenReturn("refresh");
            when(jwtService.getAccessTtlSeconds()).thenReturn(900L);

            AuthTokenResponse response = authService.login(
                    new LoginRequest("alice@example.com", "Password1"), "127.0.0.1");

            assertThat(response.accessToken()).isEqualTo("access");
            assertThat(response.refreshToken()).isEqualTo("refresh");
            assertThat(response.tokenType()).isEqualTo("Bearer");
            assertThat(response.expiresInSeconds()).isEqualTo(900L);
            verify(tokenStore).store(eq(AuthService.PURPOSE_REFRESH_TOKEN), eq("refresh"),
                    eq(id.toString()), any());
        }

        @Test
        void rejectsRateLimitedClient() {
            when(rateLimiter.tryAcquire(anyString(), anyString(), anyInt(), any()))
                    .thenReturn(false);
            when(rateLimiter.retryAfterSeconds(anyString(), anyString(), anyInt(), any()))
                    .thenReturn(420L);

            assertThatThrownBy(() -> authService.login(
                    new LoginRequest("alice@example.com", "wrong"), "1.2.3.4"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> {
                        AuthException ae = (AuthException) ex;
                        assertThat(ae.getCode()).isEqualTo("RATE_LIMITED");
                        assertThat(ae.getHttpStatus()).isEqualTo(429);
                        assertThat(ae.getMessage()).contains("420");
                    });

            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        void rejectsMissingUser() {
            when(rateLimiter.tryAcquire(anyString(), anyString(), anyInt(), any())).thenReturn(true);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(
                    new LoginRequest("ghost@example.com", "Password1"), "127.0.0.1"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("INVALID_CREDENTIALS"));
        }

        @Test
        void rejectsWrongPassword() {
            UUID id = UUID.randomUUID();
            when(rateLimiter.tryAcquire(anyString(), anyString(), anyInt(), any())).thenReturn(true);
            when(userRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(existingEmailUser(id)));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            assertThatThrownBy(() -> authService.login(
                    new LoginRequest("alice@example.com", "bad"), "127.0.0.1"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("INVALID_CREDENTIALS"));
        }

        @Test
        void rejectsOAuthOnlyAccount() {
            UUID id = UUID.randomUUID();
            User oauth = new User(id, "alice@example.com", "Alice");
            oauth.setPasswordHash(null);
            oauth.setAuthProvider(AuthProvider.GOOGLE);
            when(rateLimiter.tryAcquire(anyString(), anyString(), anyInt(), any())).thenReturn(true);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(oauth));

            assertThatThrownBy(() -> authService.login(
                    new LoginRequest("alice@example.com", "Password1"), "127.0.0.1"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("OAUTH_ONLY_ACCOUNT"));
        }
    }

    @Nested
    class Refresh {

        @Test
        void rotatesTokensOnValidRefresh() throws Exception {
            UUID id = UUID.randomUUID();
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn(id.toString());

            when(jwtService.parseAndValidate("refresh-old", "refresh")).thenReturn(claims);
            when(tokenStore.consume(AuthService.PURPOSE_REFRESH_TOKEN, "refresh-old"))
                    .thenReturn(Optional.of(id.toString()));
            when(userRepository.findById(id)).thenReturn(Optional.of(existingEmailUser(id)));
            when(jwtService.generateAccessToken(eq(id), anyString(), any())).thenReturn("access");
            when(jwtService.generateRefreshToken(id)).thenReturn("refresh-new");

            AuthTokenResponse response = authService.refresh(
                    new RefreshTokenRequest("refresh-old"));

            assertThat(response.accessToken()).isEqualTo("access");
            assertThat(response.refreshToken()).isEqualTo("refresh-new");
            verify(tokenStore).store(eq(AuthService.PURPOSE_REFRESH_TOKEN), eq("refresh-new"),
                    eq(id.toString()), any());
        }

        @Test
        void rejectsInvalidJwt() {
            when(jwtService.parseAndValidate(anyString(), anyString()))
                    .thenThrow(new IllegalArgumentException("bad"));

            assertThatThrownBy(() -> authService.refresh(
                    new RefreshTokenRequest("garbage")))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("INVALID_TOKEN"));
        }

        @Test
        void rejectsMissingStoredPayload() throws Exception {
            UUID id = UUID.randomUUID();
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn(id.toString());
            when(jwtService.parseAndValidate(anyString(), anyString())).thenReturn(claims);
            when(tokenStore.consume(anyString(), anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.refresh(
                    new RefreshTokenRequest("refresh-old")))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("INVALID_TOKEN"));
        }
    }

    @Nested
    class Logout {

        @Test
        void deletesRefreshToken() {
            authService.logout("refresh-token");
            verify(tokenStore).delete(AuthService.PURPOSE_REFRESH_TOKEN, "refresh-token");
        }

        @Test
        void nullOrBlankIsNoOp() {
            authService.logout(null);
            authService.logout("   ");
            verify(tokenStore, never()).delete(anyString(), anyString());
        }
    }

    @Nested
    class ForgotPassword {

        @Test
        void issuesTokenForEmailUser() {
            UUID id = UUID.randomUUID();
            when(userRepository.findByEmail("alice@example.com"))
                    .thenReturn(Optional.of(existingEmailUser(id)));

            authService.forgotPassword(new ForgotPasswordRequest("alice@example.com"));

            verify(tokenStore).store(eq(AuthService.PURPOSE_PASSWORD_RESET), anyString(),
                    eq(id.toString()), eq(Duration.ofHours(1)));
            verify(emailSender).send(eq("alice@example.com"), anyString(),
                    anyString(), anyString());
        }

        @Test
        void unknownEmailIsSilent() {
            when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

            authService.forgotPassword(new ForgotPasswordRequest("ghost@example.com"));

            verify(tokenStore, never()).store(anyString(), anyString(),
                    anyString(), any(Duration.class));
            verify(emailSender, never()).send(anyString(), anyString(),
                    anyString(), anyString());
        }

        @Test
        void oauthOnlyAccountIsSilent() {
            UUID id = UUID.randomUUID();
            User oauth = new User(id, "alice@example.com", "Alice");
            oauth.setAuthProvider(AuthProvider.GOOGLE);
            oauth.setPasswordHash(null);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(oauth));

            authService.forgotPassword(new ForgotPasswordRequest("alice@example.com"));

            verify(tokenStore, never()).store(anyString(), anyString(),
                    anyString(), any(Duration.class));
        }
    }

    @Nested
    class ResetPassword {

        @Test
        void rehashesPasswordForEmailUser() {
            UUID id = UUID.randomUUID();
            when(tokenStore.consume(AuthService.PURPOSE_PASSWORD_RESET, "tok"))
                    .thenReturn(Optional.of(id.toString()));
            when(userRepository.findById(id)).thenReturn(Optional.of(existingEmailUser(id)));
            when(passwordEncoder.encode("Password2")).thenReturn("$2a$12$rehashed");

            authService.resetPassword(new ResetPasswordRequest("tok", "Password2"));

            verify(userRepository).save(any());
            verify(passwordEncoder).encode("Password2");
        }

        @Test
        void rejectsOAuthOnlyAccount() {
            UUID id = UUID.randomUUID();
            User oauth = new User(id, "alice@example.com", "Alice");
            oauth.setAuthProvider(AuthProvider.GOOGLE);
            oauth.setPasswordHash(null);
            when(tokenStore.consume(anyString(), anyString()))
                    .thenReturn(Optional.of(id.toString()));
            when(userRepository.findById(id)).thenReturn(Optional.of(oauth));

            assertThatThrownBy(() -> authService.resetPassword(
                    new ResetPasswordRequest("tok", "Password2")))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("OAUTH_ONLY_ACCOUNT"));
        }

        @Test
        void rejectsUnknownToken() {
            when(tokenStore.consume(anyString(), anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.resetPassword(
                    new ResetPasswordRequest("tok", "Password2")))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("INVALID_TOKEN"));
        }
    }

    @Nested
    class GoogleOAuth {

        @Test
        void issuesTokensForExistingVerifiedUser() {
            UUID id = UUID.randomUUID();
            User existing = existingEmailUser(id);
            existing.setAuthProvider(AuthProvider.EMAIL);
            existing.setVerified(true);
            when(googleVerifier.verify("id-token")).thenReturn(
                    new OAuthIdTokenVerifier.VerifiedProfile(
                            "google", "google-sub-123", "alice@example.com", "Alice"));
            when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));
            when(jwtService.generateAccessToken(eq(id), anyString(), any())).thenReturn("access");
            when(jwtService.generateRefreshToken(id)).thenReturn("refresh");

            AuthTokenResponse response = authService.loginWithGoogle(
                    new OAuthGoogleRequest("id-token"));

            assertThat(response.accessToken()).isEqualTo("access");
            verify(userRepository, times(1)).save(existing);
        }

        @Test
        void createsNewUserWhenNoMatch() {
            when(googleVerifier.verify("id-token")).thenReturn(
                    new OAuthIdTokenVerifier.VerifiedProfile(
                            "google", "sub-1", "new@example.com", "New User"));
            when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
            when(jwtService.generateAccessToken(any(), anyString(), any())).thenReturn("access");
            when(jwtService.generateRefreshToken(any())).thenReturn("refresh");

            authService.loginWithGoogle(new OAuthGoogleRequest("id-token"));

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();
            assertThat(saved.getEmail()).isEqualTo("new@example.com");
            assertThat(saved.getAuthProvider()).isEqualTo(AuthProvider.GOOGLE);
            assertThat(saved.isVerified()).isTrue();
            assertThat(saved.getPasswordHash()).isNull();
        }

        @Test
        void missingEmailThrowsBadRequest() {
            when(googleVerifier.verify("id-token")).thenReturn(
                    new OAuthIdTokenVerifier.VerifiedProfile(
                            "google", "sub-2", null, null));
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.loginWithGoogle(
                    new OAuthGoogleRequest("id-token")))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getCode())
                            .isEqualTo("EMAIL_REQUIRED"));
        }
    }
}
