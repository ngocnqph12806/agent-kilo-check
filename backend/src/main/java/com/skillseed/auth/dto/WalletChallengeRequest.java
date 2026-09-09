package com.skillseed.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Request to issue a fresh SIWE challenge message for a given wallet
 * address and chain. The client must sign the returned message with the
 * wallet and submit it via {@link WalletVerifyRequest}.
 */
public record WalletChallengeRequest(
        @NotBlank
        @Pattern(regexp = "^0x[a-fA-F0-9]{40}$",
                 message = "address must be a 0x-prefixed 40-char hex string")
        String address,

        @Positive
        long chainId
) {
}
