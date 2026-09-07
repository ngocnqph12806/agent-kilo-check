package com.skillseed.auth.service;

import com.skillseed.auth.exception.AuthException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verifies Google ID tokens via Google's JWKS endpoint (RS256). The set of
 * trusted audiences is configurable (one or more OAuth client IDs).
 */
@Configuration
public class OAuthIdTokenVerifierConfig {

    private static final Logger log = LoggerFactory.getLogger(OAuthIdTokenVerifierConfig.class);
    private static final String GOOGLE_ISSUERS = "https://accounts.google.com,accounts.google.com";
    private static final String GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs";

    @Bean
    @ConditionalOnProperty(prefix = "oauth.google", name = "client-id")
    public OAuthIdTokenVerifier googleIdTokenVerifier(
            @Value("${oauth.google.client-id}") String clientId) {
        return new GoogleIdTokenVerifier(clientId);
    }

    @Bean
    @ConditionalOnProperty(prefix = "oauth.apple", name = "client-id")
    public OAuthIdTokenVerifier appleIdTokenVerifier(
            @Value("${oauth.apple.client-id}") String clientId,
            @Value("${oauth.apple.issuer:https://appleid.apple.com}") String issuer) {
        return new AppleIdTokenVerifier(clientId, issuer);
    }

    /** Base class for JWKS-based ID token verification. */
    abstract static class JwksIdTokenVerifier implements OAuthIdTokenVerifier {

        private final RestClient rest = RestClient.create();
        private final Map<String, Key> keyCache = new ConcurrentHashMap<>();
        private volatile long keysFetchedAt = 0L;

        protected abstract String jwksUrl();
        protected abstract List<String> issuers();
        protected abstract List<String> audiences();

        protected Key getKey(String kid) {
            long now = System.currentTimeMillis();
            if (kid == null || (now - keysFetchedAt) > 3_600_000L) {
                refreshKeys();
            }
            Key k = keyCache.get(kid);
            if (k == null) {
                refreshKeys();
                k = keyCache.get(kid);
            }
            return k;
        }

        @SuppressWarnings("unchecked")
        private void refreshKeys() {
            try {
                Map<String, Object> body = rest.get().uri(jwksUrl()).retrieve().body(Map.class);
                if (body == null) {
                    throw new IllegalStateException("Empty JWKS");
                }
                List<Map<String, Object>> keys = (List<Map<String, Object>>) body.get("keys");
                Map<String, Key> fresh = new HashMap<>();
                for (Map<String, Object> jwk : keys) {
                    String kid = (String) jwk.get("kid");
                    String n = (String) jwk.get("n");
                    String e = (String) jwk.get("e");
                    if (kid == null || n == null || e == null) {
                        continue;
                    }
                    BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
                    BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
                    Key key = KeyFactory.getInstance("RSA")
                            .generatePublic(new RSAPublicKeySpec(modulus, exponent));
                    fresh.put(kid, key);
                }
                keyCache.clear();
                keyCache.putAll(fresh);
                keysFetchedAt = System.currentTimeMillis();
            } catch (Exception ex) {
                throw AuthException.unauthorized("INVALID_OAUTH_TOKEN",
                        "Unable to fetch JWKS: " + ex.getMessage());
            }
        }

        @Override
        public VerifiedProfile verify(String idToken) {
            try {
                Jws<io.jsonwebtoken.Claims> jws = Jwts.parser()
                        .keyLocator(headers -> {
                            String kid = headers.getKeyId();
                            Key key = getKey(kid);
                            if (key == null) {
                                throw new IllegalStateException("Unknown kid: " + kid);
                            }
                            return key;
                        })
                        .requireIssuer(issuers().toArray(new String[0]))
                        .requireAudience(audiences().toArray(new String[0]))
                        .build()
                        .parseSignedClaims(idToken);
                io.jsonwebtoken.Claims claims = jws.getPayload();

                String sub = claims.get("sub", String.class);
                String email = claims.get("email", String.class);
                Boolean verified = claims.get("email_verified", Boolean.class);
                if (sub == null || sub.isBlank()) {
                    throw AuthException.unauthorized("INVALID_OAUTH_TOKEN",
                            "Missing sub claim");
                }
                if (email != null && verified != null && !verified) {
                    throw AuthException.unauthorized("INVALID_OAUTH_TOKEN",
                            "Email not verified by provider");
                }
                String name = claims.get("name", String.class);
                return new VerifiedProfile(providerName(), sub, email, name);
            } catch (AuthException ex) {
                throw ex;
            } catch (Exception ex) {
                log.debug("OAuth verification failed: {}", ex.getMessage());
                throw AuthException.unauthorized("INVALID_OAUTH_TOKEN",
                        "OAuth id_token is invalid");
            }
        }

        protected abstract String providerName();
    }

    /** Google sign-in: issuer = accounts.google.com, audience = client id. */
    static final class GoogleIdTokenVerifier extends JwksIdTokenVerifier {

        private final String clientId;

        GoogleIdTokenVerifier(String clientId) {
            this.clientId = clientId;
        }

        @Override
        protected String jwksUrl() {
            return GOOGLE_JWKS_URL;
        }

        @Override
        protected List<String> issuers() {
            return List.of(GOOGLE_ISSUERS.split(","));
        }

        @Override
        protected List<String> audiences() {
            return List.of(clientId);
        }

        @Override
        protected String providerName() {
            return "google";
        }

        @Override
        public Map<String, String> configSummary() {
            return Map.of("provider", "google", "clientId", clientId,
                    "jwksUrl", GOOGLE_JWKS_URL);
        }
    }

    /** Apple sign-in: issuer = appleid.apple.com, audience = Service ID or App ID. */
    static final class AppleIdTokenVerifier extends JwksIdTokenVerifier {

        private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";

        private final String clientId;
        private final String issuer;

        AppleIdTokenVerifier(String clientId, String issuer) {
            this.clientId = clientId;
            this.issuer = issuer;
        }

        @Override
        protected String jwksUrl() {
            return APPLE_JWKS_URL;
        }

        @Override
        protected List<String> issuers() {
            return List.of(issuer);
        }

        @Override
        protected List<String> audiences() {
            return List.of(clientId);
        }

        @Override
        protected String providerName() {
            return "apple";
        }

        @Override
        public Map<String, String> configSummary() {
            return Map.of("provider", "apple", "clientId", clientId,
                    "issuer", issuer, "jwksUrl", APPLE_JWKS_URL);
        }
    }
}