package com.skillseed.auth.service;

import java.time.Duration;

/**
 * Rate limiter abstraction. Implementations count attempts per
 * (purpose, key) tuple and reject callers that exceed the configured
 * quota within the configured window.
 */
public interface RateLimiter {

    /**
     * Records an attempt against {@code key} for {@code purpose} and
     * returns {@code true} if the caller is still within the limit.
     */
    boolean tryAcquire(String purpose, String key, int maxAttempts, Duration window);

    /**
     * Returns the number of seconds the caller must wait before retrying.
     * Zero when the caller is currently allowed.
     */
    long retryAfterSeconds(String purpose, String key, int maxAttempts, Duration window);
}