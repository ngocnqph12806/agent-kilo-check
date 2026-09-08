package com.skillseed.session.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Verifies the {@code X-Daily-Signature} header on incoming Daily.co
 * webhooks. Daily signs the raw request body with HMAC-SHA256 using
 * the signing key configured in {@code session.daily.webhook-signing-key}.
 *
 * <p>Defense against spoofed payloads that could otherwise flip a
 * booking to {@code COMPLETED} and trigger a wallet capture.
 */
public final class DailyWebhookSignatureVerifier {

    private DailyWebhookSignatureVerifier() {
    }

    public static boolean verify(byte[] rawBody,
                                 String signatureHeader,
                                 String signingKey) {
        if (rawBody == null || rawBody.length == 0
                || signatureHeader == null || signatureHeader.isBlank()
                || signingKey == null || signingKey.isBlank()) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expected = mac.doFinal(rawBody);
            byte[] actual = HexFormat.of().parseHex(signatureHeader.trim().toLowerCase());
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception ex) {
            return false;
        }
    }
}
