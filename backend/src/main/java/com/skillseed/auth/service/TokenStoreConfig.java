package com.skillseed.auth.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * Redis-backed implementation of {@link TokenStore} used in dev/staging/prod.
 * Falls back to an in-memory implementation when Redis is unavailable so the
 * app still boots locally without infrastructure (used in tests).
 */
@Configuration
public class TokenStoreConfig {

    /**
     * The "purpose" namespace is encoded into the Redis key as
     * {@code skillseed:auth:{purpose}:{token}}.
     */
    static String key(String purpose, String token) {
        return "skillseed:auth:" + purpose + ":" + token;
    }

    @Bean
    @ConditionalOnMissingBean(TokenStore.class)
    public TokenStore tokenStore(StringRedisTemplate redisTemplate) {
        return new RedisTokenStore(redisTemplate);
    }

    /**
     * Redis-backed token store. Atomic consume is implemented via Lua
     * to guarantee GET+DEL semantics in a single round-trip.
     */
    public static final class RedisTokenStore implements TokenStore {

        private static final String CONSUME_LUA =
                "local v = redis.call('GET', KEYS[1]); "
                        + "if v then redis.call('DEL', KEYS[1]); end; "
                        + "return v";

        private final StringRedisTemplate redis;

        public RedisTokenStore(StringRedisTemplate redis) {
            this.redis = Objects.requireNonNull(redis);
        }

        @Override
        public void store(String purpose, String token, String payload, Duration ttl) {
            redis.opsForValue().set(key(purpose, token), payload, ttl);
        }

        @Override
        public Optional<String> peek(String purpose, String token) {
            String value = redis.opsForValue().get(key(purpose, token));
            return Optional.ofNullable(value);
        }

        @Override
        public Optional<String> consume(String purpose, String token) {
            String key = key(purpose, token);
            String value = redis.execute(
                    (org.springframework.data.redis.core.RedisCallback<String>) connection -> {
                        return (String) connection.scriptingCommands().eval(
                                CONSUME_LUA.getBytes(),
                                ReturnType.VALUE,
                                1,
                                key.getBytes());
                    });
            return Optional.ofNullable(value);
        }

        @Override
        public void delete(String purpose, String token) {
            redis.delete(key(purpose, token));
        }
    }
}
