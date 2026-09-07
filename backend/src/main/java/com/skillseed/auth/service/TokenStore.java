package com.skillseed.auth.service;

import java.time.Duration;
import java.util.Optional;

/**
 * Short-lived token storage abstraction used for email verification and
 * password reset. Backed by Redis in production; implementation must be
 * thread-safe.
 *
 * <p>Tokens are stored as opaque strings; payload (e.g. user id) is opaque
 * to the caller.
 */
public interface TokenStore {

    /**
     * Store a token under {@code purpose} (e.g. {@code "email-verify"})
     * with the given payload. Existing payload for the same token is
     * overwritten.
     */
    void store(String purpose, String token, String payload, Duration ttl);

    /**
     * Returns the payload associated with the token, or empty if the token
     * does not exist or has expired. Does NOT consume the token.
     */
    Optional<String> peek(String purpose, String token);

    /**
     * Returns the payload and atomically deletes the token (single-use
     * semantics). Returns empty if the token does not exist or has
     * expired.
     */
    Optional<String> consume(String purpose, String token);

    /**
     * Deletes a token explicitly (e.g. after a successful verification).
     */
    void delete(String purpose, String token);
}