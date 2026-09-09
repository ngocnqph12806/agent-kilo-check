package com.skillseed.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Verifies a signed SIWE challenge. The client sends the exact message it
 * received from {@link WalletChallengeResponse} and the 0x-prefixed
 * 65-byte signature. The backend recovers the signer address, links (or
 * creates) the user, and returns a JWT pair on success.
 */
public record WalletVerifyRequest(
        @NotBlank String message,
        @NotBlank
        @Pattern(regexp = "^0x[a-fA-F0-9]+$",
                 message = "signature must be 0x-prefixed hex")
        String signature,
        @Positive long chainId
) {
}
