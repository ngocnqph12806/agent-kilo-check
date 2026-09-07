package com.skillseed.auth.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

/**
 * Cryptographically strong opaque token generator used for short-lived
 * single-use tokens (email verification, password reset). Returns a
 * URL-safe Base64-encoded random string.
 */
public final class TokenGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DEFAULT_BYTE_LENGTH = 32;

    private TokenGenerator() {
    }

    public static String generate() {
        return generate(DEFAULT_BYTE_LENGTH);
    }

    public static String generate(int byteLength) {
        byte[] bytes = new byte[byteLength];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static Duration defaultTtl() {
        return Duration.ofHours(24);
    }
}