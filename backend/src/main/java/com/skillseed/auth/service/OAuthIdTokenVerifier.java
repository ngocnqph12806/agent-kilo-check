package com.skillseed.auth.service;

import java.util.Map;

/**
 * Verifies OAuth provider ID tokens (Google / Apple) and returns the
 * subject + email. Implementations are responsible for fetching the
 * provider's JWKS, validating signature / issuer / audience / expiry,
 * and rejecting any untrusted token.
 */
public interface OAuthIdTokenVerifier {

    /**
     * Result of a successful ID token verification.
     *
     * @param provider "google" or "apple"
     * @param subject  stable per-provider user id (sub claim)
     * @param email    verified email (may be null if the provider did
     *                 not return one and privacy scopes allow)
     * @param name     optional display name (Apple only on first login)
     */
    record VerifiedProfile(
            String provider,
            String subject,
            String email,
            String name) {
    }

    /**
     * @throws com.skillseed.auth.exception.AuthException with code
     *     {@code INVALID_OAUTH_TOKEN} if the token is not valid.
     */
    VerifiedProfile verify(String idToken);

    /**
     * Used to surface the expected issuer / audience values so operators
     * can confirm the configured credentials match the provider.
     */
    Map<String, String> configSummary();
}