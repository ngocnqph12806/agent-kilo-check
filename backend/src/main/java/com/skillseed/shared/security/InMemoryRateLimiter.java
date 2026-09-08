package com.skillseed.shared.security;

import com.skillseed.auth.exception.AuthException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tiny in-process rate limiter used by auth endpoints that should be
 * throttled against brute-force / email-bombing / token-spraying (see
 * FR-M07 forgot-password and FR-M06 verify-email).
 *
 * <p>Each call increments a counter for the supplied {@code key}; if the
 * counter exceeds {@code limit} within a {@code window} an
 * {@link AuthException} (HTTP 429) is thrown. The window is fixed (not
 * sliding): counters reset at deterministic timestamps, which is good
 * enough for Phase 1. Switch to a Redis-backed bucket if you need
 * horizontal scaling.
 *
 * <p>State is held in memory; on multi-instance deployments every pod
 * enforces its own quota — the worst-case is {@code N × limit}, which is
 * acceptable for the auth endpoints where N is small.
 */
@Service
public class InMemoryRateLimiter {

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    /**
     * Acquire one slot under {@code key}, otherwise throw
     * {@link AuthException#tooManyRequests} (HTTP 429).
     *
     * @param key     stable identifier (e.g. "forgot-password:foo@bar.com")
     * @param limit   max acquisitions per window
     * @param window  window size
     * @param scopeCode error code stamped on the thrown exception (e.g.
     *                 {@code RATE_LIMIT_FORGOT_PASSWORD})
     */
    public void acquireOrThrow(String key, int limit, Duration window, String scopeCode) {
        Instant now = Instant.now();
        Counter updated = counters.compute(key, (k, existing) -> {
            if (existing == null
                    || Duration.between(existing.startedAt(), now).compareTo(window) > 0) {
                return new Counter(now, 1);
            }
            return new Counter(existing.startedAt(), existing.count() + 1);
        });
        if (updated.count() > limit) {
            throw AuthException.tooManyRequests(scopeCode,
                    "Too many attempts; please wait before trying again.");
        }
    }

    /** Test/admin hook: drop all counters. */
    public void reset() {
        counters.clear();
    }

    private record Counter(Instant startedAt, int count) { }
}
