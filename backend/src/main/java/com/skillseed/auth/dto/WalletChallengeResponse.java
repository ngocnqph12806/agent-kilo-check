package com.skillseed.auth.dto;

/**
 * Response to {@link WalletChallengeRequest}: the SIWE message the client
 * must sign, plus the chain id it was bound to. The client echoes the
 * message back unchanged in {@link WalletVerifyRequest#message()}.
 */
public record WalletChallengeResponse(
        String message,
        long chainId
) {
}
