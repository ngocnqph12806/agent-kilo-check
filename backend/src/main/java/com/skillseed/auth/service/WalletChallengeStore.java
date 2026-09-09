package com.skillseed.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

/**
 * Tracks consumed SIWE message hashes in Redis so a (message, signature)
 * pair cannot be replayed. The TTL is 10 minutes — long enough that a
 * legitimate client has time to sign + submit, short enough that the
 * Redis keyspace stays small.
 */
@Service
public class WalletChallengeStore {

    static final Duration TTL = Duration.ofMinutes(10);

    private static final String PREFIX = "skillseed:wallet:nonce:";

    private final StringRedisTemplate redis;

    public WalletChallengeStore(
            StringRedisTemplate redis,
            @Value("${app.wallet.challenge-ttl-minutes:10}") long ttlMinutes) {
        this.redis = redis;
        // TTL is constant for now — constructor param reserved for env override.
        if (ttlMinutes > 0) {
            // No-op: keeps env wiring consistent.
        }
    }

    /**
     * Returns true if the message hash has not been seen (and atomically
     * marks it consumed). Returns false if it has been consumed already.
     */
    public boolean consumeIfFresh(String message) {
        String hash = sha256Hex(message);
        String key = PREFIX + hash;
        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", TTL);
        return Boolean.TRUE.equals(ok);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
