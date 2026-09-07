package com.skillseed.user.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response body for {@code POST /api/v1/users/me/onboarding}.
 */
public record OnboardingCompleteResponse(
        UUID userId,
        boolean onboardingCompleted,
        WalletGrant wallet) {

    /**
     * Confirms the starter-seeds grant: amount credited, when it expires,
     * and the resulting balance.
     */
    public record WalletGrant(
            int amount,
            Instant expiresAt,
            int newBalance) {
    }
}