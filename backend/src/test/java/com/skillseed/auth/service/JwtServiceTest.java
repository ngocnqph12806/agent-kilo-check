package com.skillseed.auth.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for {@link JwtService}. Exercises token generation,
 * validation, and the secret-length guard.
 */
class JwtServiceTest {

    private static final String STRONG_SECRET =
            "test_secret_must_be_at_least_32_bytes_for_hs256_security_requirements_xx";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(STRONG_SECRET, 15, 30);
        jwtService.init();
    }

    @Test
    void shortSecretFailsAtInit() {
        JwtService shortJwt = new JwtService("too-short", 15, 30);
        assertThatThrownBy(shortJwt::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }

    @Test
    void accessTokenRoundTripContainsExpectedClaims() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateAccessToken(userId, "user@example.com", (short) 1);

        Claims claims = jwtService.parseAndValidate(token, "access");

        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("email", String.class)).isEqualTo("user@example.com");
        assertThat(claims.get("verificationLevel", Short.class)).isEqualTo((short) 1);
        assertThat(claims.get("tokenType", String.class)).isEqualTo("access");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void refreshTokenRoundTripContainsExpectedClaims() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateRefreshToken(userId);

        Claims claims = jwtService.parseAndValidate(token, "refresh");

        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("tokenType", String.class)).isEqualTo("refresh");
        assertThat(claims.get("email")).isNull();
    }

    @Test
    void parsingWithWrongTypeFails() {
        String access = jwtService.generateAccessToken(UUID.randomUUID(), "x@y.com", (short) 0);
        assertThatThrownBy(() -> jwtService.parseAndValidate(access, "refresh"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("token type");
    }

    @Test
    void ttlHelpersMatchConfiguredValues() {
        assertThat(jwtService.getAccessTtlSeconds()).isEqualTo(15L * 60);
        assertThat(jwtService.getRefreshTtlSeconds()).isEqualTo(30L * 24 * 60 * 60);
    }

    @Test
    void tamperedSignatureFailsValidation() {
        String token = jwtService.generateAccessToken(UUID.randomUUID(), "x@y.com", (short) 0);
        String tampered = token.substring(0, token.length() - 4) + "AAAA";
        assertThatThrownBy(() -> jwtService.parseAndValidate(tampered, "access"))
                .isInstanceOf(Exception.class);
    }
}
