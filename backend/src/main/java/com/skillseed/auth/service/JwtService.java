package com.skillseed.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Issues and validates HS256 JWT tokens (access + refresh) used by the auth module.
 *
 * <p>Token TTLs are configurable via {@code jwt.access-token-ttl-minutes}
 * (default 15) and {@code jwt.refresh-token-ttl-days} (default 30).
 */
@Service
public class JwtService {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_VERIFICATION_LEVEL = "verificationLevel";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final String secret;
    private final long accessTtlMinutes;
    private final long refreshTtlDays;

    private SecretKey signingKey;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-ttl-minutes:15}") long accessTtlMinutes,
            @Value("${jwt.refresh-token-ttl-days:30}") long refreshTtlDays) {
        this.secret = secret;
        this.accessTtlMinutes = accessTtlMinutes;
        this.refreshTtlDays = refreshTtlDays;
    }

    @PostConstruct
    void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret must be at least 32 bytes for HS256");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UUID userId, String email, short verificationLevel) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(accessTtlMinutes));
        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_VERIFICATION_LEVEL, verificationLevel)
                .claim(CLAIM_TOKEN_TYPE, TYPE_ACCESS)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofDays(refreshTtlDays));
        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_TOKEN_TYPE, TYPE_REFRESH)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseAndValidate(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String type = claims.get(CLAIM_TOKEN_TYPE, String.class);
        if (!expectedType.equals(type)) {
            throw new IllegalArgumentException("Unexpected token type: " + type);
        }
        return claims;
    }

    public long getAccessTtlSeconds() {
        return Duration.ofMinutes(accessTtlMinutes).toSeconds();
    }

    public long getRefreshTtlSeconds() {
        return Duration.ofDays(refreshTtlDays).toSeconds();
    }
}