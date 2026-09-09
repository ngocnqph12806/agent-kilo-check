package com.skillseed.auth.service;

import com.skillseed.auth.exception.AuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Builds and verifies EIP-4361 (Sign-In with Ethereum / SIWE) messages.
 *
 * <p>The challenge is the SIWE message itself; the user signs it with their
 * wallet (personal_sign). We verify by recovering the signer address from
 * the (message, signature) pair and comparing it (case-insensitive) to the
 * {@code address} claim embedded in the message.
 *
 * <p>Phase 1 only supports Ethereum mainnet (chain id 1) and Sepolia
 * (chain id 11155111); other chains return {@code UNSUPPORTED_CHAIN}.
 */
@Service
public class SiweService {

    private static final Logger log = LoggerFactory.getLogger(SiweService.class);

    private static final Pattern ADDRESS_PATTERN =
            Pattern.compile("^0x[a-fA-F0-9]{40}$");

    /** Allowed chain ids for SIWE in Phase 1. */
    static final long[] ALLOWED_CHAIN_IDS = new long[]{1L, 11155111L};

    private final String domain;
    private final String uri;
    private final SecureRandom random = new SecureRandom();

    public SiweService(@Value("${app.siwe.domain:skillseed.local}") String domain,
                       @Value("${app.siwe.uri:http://localhost:3000}") String uri) {
        this.domain = domain;
        this.uri = uri;
    }

    /**
     * Builds a fresh SIWE message addressed to {@code address}. The nonce
     * is a 16-hex-char (8-byte) random value; the issued-at timestamp is
     * ISO-8601 UTC.
     */
    public String buildMessage(String address, long chainId) {
        validateAddress(address);
        validateChain(chainId);
        String nonce = generateNonce();
        String issuedAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        return new StringBuilder()
                .append(domain).append(" wants you to sign in with your Ethereum account:\n")
                .append(address).append("\n\n")
                .append("Sign in to SkillSeed — no password required.\n\n")
                .append("URI: ").append(uri).append('\n')
                .append("Version: 1\n")
                .append("Chain ID: ").append(chainId).append('\n')
                .append("Nonce: ").append(nonce).append('\n')
                .append("Issued At: ").append(issuedAt)
                .toString();
    }

    /**
     * Recovers the signer's Ethereum address from a (message, signature)
     * pair. Returns the address as a 0x-prefixed lower-case hex string.
     *
     * <p>Uses {@link Sign#signedPrefixedMessageToKey} which prepends the
     * {@code "Ethereum Signed Message:\n"} prefix, applies
     * keccak256, and recovers the secp256k1 public key. The address is
     * derived by keccak256-hashing the 64-byte public key and taking the
     * last 20 bytes (per Ethereum convention).
     *
     * @throws AuthException with code {@code INVALID_SIGNATURE} if the
     *                       signature is malformed or recovery fails.
     */
    public String recoverAddress(String message, String signatureHex) {
        if (signatureHex == null || signatureHex.isBlank()) {
            throw AuthException.unauthorized("INVALID_SIGNATURE",
                "Signature is required");
        }
        byte[] signature;
        try {
            signature = Numeric.hexStringToByteArray(signatureHex);
        } catch (Exception ex) {
            throw AuthException.unauthorized("INVALID_SIGNATURE",
                "Signature is not valid hex");
        }
        if (signature.length != 65) {
            throw AuthException.unauthorized("INVALID_SIGNATURE",
                "Signature must be 65 bytes (got " + signature.length + ")");
        }
        try {
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            Sign.SignatureData sigData = new Sign.SignatureData(
                    signature[64],
                    Arrays.copyOfRange(signature, 0, 32),
                    Arrays.copyOfRange(signature, 32, 64));
            BigInteger publicKey = Sign.signedPrefixedMessageToKey(messageBytes, sigData);
            return Keys.getAddress(publicKey);
        } catch (Exception ex) {
            log.warn("SIWE signature recovery failed: {}", ex.getMessage());
            throw AuthException.unauthorized("INVALID_SIGNATURE",
                "Signature could not be recovered");
        }
    }

    /** Validates the {@code address} claim embedded in the SIWE message. */
    public void validateAddress(String address) {
        if (address == null || !ADDRESS_PATTERN.matcher(address).matches()) {
            throw AuthException.badRequest("INVALID_ADDRESS",
                "Wallet address must be a 0x-prefixed 40-char hex string");
        }
    }

    public void validateChain(long chainId) {
        for (long allowed : ALLOWED_CHAIN_IDS) {
            if (allowed == chainId) {
                return;
            }
        }
        throw AuthException.badRequest("UNSUPPORTED_CHAIN",
            "Only Ethereum mainnet (1) and Sepolia (11155111) are supported");
    }

    private String generateNonce() {
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(16);
        for (byte b : bytes) {
            sb.append(String.format(Locale.ROOT, "%02x", b));
        }
        return sb.toString();
    }
}
