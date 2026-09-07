package com.skillseed.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end integration tests for {@code /api/v1/auth/**}.
 *
 * <p>Boots the full Spring context against ephemeral PostgreSQL and Redis
 * containers, applies the Flyway migrations, and exercises every public
 * auth endpoint. Mirrors the manual e2e checklist in
 * {@code docs/MANUAL_E2E_AUTH.md}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Testcontainers
class AuthControllerIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("skillseed")
                    .withUsername("skillseed")
                    .withPassword("skillseed_it_password");

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379).toString());
        registry.add("jwt.secret",
                () -> "it_secret_must_be_at_least_32_bytes_for_hs256_security_requirements_xx");
        registry.add("app.public-base-url", () -> "https://app.skillseed.test");
        registry.add("notification.resend.api-key", () -> "");
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    private String url(String path) {
        return "http://localhost:" + port + "/api/v1/auth" + path;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private ResponseEntity<String> post(String path, Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return rest.exchange(url(path), HttpMethod.POST,
                    new HttpEntity<>(json, jsonHeaders()), String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private ResponseEntity<String> post(String path, Object body, String forwardedFor) {
        HttpHeaders headers = jsonHeaders();
        if (forwardedFor != null) {
            headers.add("X-Forwarded-For", forwardedFor);
        }
        try {
            String json = objectMapper.writeValueAsString(body);
            return rest.exchange(url(path), HttpMethod.POST,
                    new HttpEntity<>(json, headers), String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private JsonNode parseBody(ResponseEntity<String> response) throws Exception {
        return objectMapper.readTree(response.getBody() == null ? "{}" : response.getBody());
    }

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    private Map<String, String> validRegisterBody() {
        return Map.of(
                "email", "alice@example.com",
                "password", "Password1",
                "fullName", "Alice Tester"
        );
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        void validRequestReturns201() throws Exception {
            ResponseEntity<String> response = post("/register", validRegisterBody());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(parseBody(response).get("message").asText())
                    .contains("Verification email sent");
            assertThat(userRepository.existsByEmail("alice@example.com")).isTrue();
        }

        @Test
        void duplicateEmailReturns409() {
            post("/register", validRegisterBody());

            ResponseEntity<String> response = post("/register", validRegisterBody());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        void invalidEmailReturns400() {
            Map<String, String> body = Map.of(
                    "email", "not-an-email",
                    "password", "Password1",
                    "fullName", "Bob"
            );
            ResponseEntity<String> response = post("/register", body);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        void weakPasswordReturns400() {
            Map<String, String> body = Map.of(
                    "email", "bob@example.com",
                    "password", "nodigit",
                    "fullName", "Bob"
            );
            ResponseEntity<String> response = post("/register", body);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        private void registerAlice() {
            post("/register", validRegisterBody());
            // We can't easily flip verified=true without a separate verify-email flow,
            // but login should still succeed (verified flag is informational).
        }

        @Test
        void unknownEmailReturns401() {
            ResponseEntity<String> response = post("/login",
                    Map.of("email", "ghost@example.com", "password", "Password1"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void wrongPasswordReturns401() {
            registerAlice();
            ResponseEntity<String> response = post("/login",
                    Map.of("email", "alice@example.com", "password", "wrong"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void correctCredentialsReturnTokens() throws Exception {
            registerAlice();
            ResponseEntity<String> response = post("/login",
                    Map.of("email", "alice@example.com", "password", "Password1"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            JsonNode body = parseBody(response);
            assertThat(body.get("accessToken").asText()).isNotBlank();
            assertThat(body.get("refreshToken").asText()).isNotBlank();
            assertThat(body.get("tokenType").asText()).isEqualTo("Bearer");
            assertThat(body.get("user").get("email").asText()).isEqualTo("alice@example.com");
        }

        @Test
        void rateLimitAfter5FailedAttempts() {
            registerAlice();
            String ip = "203.0.113.7";
            for (int i = 0; i < 5; i++) {
                ResponseEntity<String> failed = post("/login",
                        Map.of("email", "alice@example.com", "password", "wrong"), ip);
                assertThat(failed.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            }
            ResponseEntity<String> sixth = post("/login",
                    Map.of("email", "alice@example.com", "password", "wrong"), ip);
            assertThat(sixth.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @Nested
    @DisplayName("refresh")
    class Refresh {

        @Test
        void validRefreshRotatesAndReturnsNewPair() throws Exception {
            post("/register", validRegisterBody());
            ResponseEntity<String> login = post("/login",
                    Map.of("email", "alice@example.com", "password", "Password1"));
            String oldRefresh = parseBody(login).get("refreshToken").asText();

            ResponseEntity<String> refreshResponse = post("/refresh",
                    Map.of("refreshToken", oldRefresh));

            assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String newRefresh = parseBody(refreshResponse).get("refreshToken").asText();
            assertThat(newRefresh).isNotEqualTo(oldRefresh);
        }

        @Test
        void replayOfOldRefreshReturns401() throws Exception {
            post("/register", validRegisterBody());
            ResponseEntity<String> login = post("/login",
                    Map.of("email", "alice@example.com", "password", "Password1"));
            String refresh = parseBody(login).get("refreshToken").asText();

            post("/refresh", Map.of("refreshToken", refresh));
            ResponseEntity<String> replay = post("/refresh",
                    Map.of("refreshToken", refresh));
            assertThat(replay.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void garbageTokenReturns401() {
            ResponseEntity<String> response = post("/refresh",
                    Map.of("refreshToken", "not-a-real-jwt"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("forgot/reset password")
    class ForgotReset {

        @Test
        void forgotPasswordAlwaysReturns200() {
            post("/register", validRegisterBody());
            ResponseEntity<String> response = post("/forgot-password",
                    Map.of("email", "alice@example.com"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void forgotPasswordOnUnknownEmailAlsoReturns200() {
            ResponseEntity<String> response = post("/forgot-password",
                    Map.of("email", "nobody@example.com"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void verifyEmailWithInvalidTokenReturns400() {
            ResponseEntity<String> response = post("/verify-email",
                    Map.of("token", "not-a-real-token"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("OAuth providers")
    class OAuthProviders {

        @Test
        void googleReturns400WhenProviderNotConfigured() {
            ResponseEntity<String> response = post("/oauth/google",
                    Map.of("idToken", "fake.google.id.token"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        void appleReturns400WhenProviderNotConfigured() {
            ResponseEntity<String> response = post("/oauth/apple",
                    Map.of("idToken", "fake.apple.id.token"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("logout")
    class Logout {

        @Test
        void logoutRevokesRefreshToken() throws Exception {
            post("/register", validRegisterBody());
            ResponseEntity<String> login = post("/login",
                    Map.of("email", "alice@example.com", "password", "Password1"));
            String refresh = parseBody(login).get("refreshToken").asText();

            ResponseEntity<String> logout = post("/logout",
                    Map.of("refreshToken", refresh));
            assertThat(logout.getStatusCode()).isEqualTo(HttpStatus.OK);

            ResponseEntity<String> refreshAfter = post("/refresh",
                    Map.of("refreshToken", refresh));
            assertThat(refreshAfter.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}
