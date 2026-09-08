package com.skillseed.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.skillseed.auth.exception.AuthException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryRateLimiterTest {

    private InMemoryRateLimiter limiter;

    @BeforeEach
    void setUp() {
        limiter = new InMemoryRateLimiter();
    }

    @Test
    void allowsRequestsUpToLimit() {
        for (int i = 0; i < 5; i++) {
            limiter.acquireOrThrow("forgot-password:alice@x.com", 5,
                    Duration.ofMinutes(15), "RATE_LIMIT_FORGOT_PASSWORD");
        }
    }

    @Test
    void throwsAfterLimitExceeded() {
        for (int i = 0; i < 5; i++) {
            limiter.acquireOrThrow("forgot-password:bob@x.com", 5,
                    Duration.ofMinutes(15), "RATE_LIMIT_FORGOT_PASSWORD");
        }
        assertThatThrownBy(() ->
                limiter.acquireOrThrow("forgot-password:bob@x.com", 5,
                        Duration.ofMinutes(15), "RATE_LIMIT_FORGOT_PASSWORD"))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getHttpStatus()).isEqualTo(429);
                    assertThat(ae.getCode()).isEqualTo("RATE_LIMIT_FORGOT_PASSWORD");
                });
    }

    @Test
    void separateKeysAreIndependent() {
        for (int i = 0; i < 3; i++) {
            limiter.acquireOrThrow("verify-email:1.2.3.4", 3,
                    Duration.ofMinutes(15), "RATE_LIMIT_VERIFY_EMAIL");
        }
        // A different key should still have its full quota.
        limiter.acquireOrThrow("verify-email:5.6.7.8", 3,
                Duration.ofMinutes(15), "RATE_LIMIT_VERIFY_EMAIL");
    }

    @Test
    void windowExpiryResetsCounter() throws InterruptedException {
        // Use a 100ms window so the test stays fast.
        for (int i = 0; i < 3; i++) {
            limiter.acquireOrThrow("verify-email:fast", 3,
                    Duration.ofMillis(100), "RATE_LIMIT_VERIFY_EMAIL");
        }
        Thread.sleep(150);
        // After the window elapses, the key is allowed again.
        limiter.acquireOrThrow("verify-email:fast", 3,
                Duration.ofMillis(100), "RATE_LIMIT_VERIFY_EMAIL");
    }

    @Test
    void resetClearsAllCounters() {
        for (int i = 0; i < 2; i++) {
            limiter.acquireOrThrow("verify-email:reset", 2,
                    Duration.ofMinutes(15), "RATE_LIMIT_VERIFY_EMAIL");
        }
        limiter.reset();
        limiter.acquireOrThrow("verify-email:reset", 2,
                Duration.ofMinutes(15), "RATE_LIMIT_VERIFY_EMAIL");
    }
}
