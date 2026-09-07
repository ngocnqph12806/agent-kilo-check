package com.skillseed.auth.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Objects;

/**
 * Redis-backed fixed-window rate limiter. The window is stored as a Redis
 * key {@code skillseed:rl:{purpose}:{key}} with TTL equal to the window;
 * INCR is used to count attempts. When the count exceeds {@code maxAttempts}
 * the caller is rate-limited until the window expires.
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    public RateLimiter rateLimiter(StringRedisTemplate redisTemplate) {
        return new RedisRateLimiter(redisTemplate);
    }

    public static final class RedisRateLimiter implements RateLimiter {

        private final StringRedisTemplate redis;

        public RedisRateLimiter(StringRedisTemplate redis) {
            this.redis = Objects.requireNonNull(redis);
        }

        @Override
        public boolean tryAcquire(String purpose, String key, int maxAttempts, Duration window) {
            String redisKey = redisKey(purpose, key);
            Long current = redis.opsForValue().increment(redisKey);
            if (current == null) {
                return false;
            }
            if (current == 1L) {
                redis.expire(redisKey, window);
            }
            return current <= maxAttempts;
        }

        @Override
        public long retryAfterSeconds(String purpose, String key, int maxAttempts, Duration window) {
            String redisKey = redisKey(purpose, key);
            String raw = redis.opsForValue().get(redisKey);
            if (raw == null) {
                return 0;
            }
            long current;
            try {
                current = Long.parseLong(raw);
            } catch (NumberFormatException ex) {
                return 0;
            }
            if (current <= maxAttempts) {
                return 0;
            }
            Long ttlSeconds = redis.getExpire(redisKey);
            return ttlSeconds == null || ttlSeconds < 0 ? window.toSeconds() : ttlSeconds;
        }

        private String redisKey(String purpose, String key) {
            return "skillseed:rl:" + purpose + ":" + key;
        }
    }
}