package com.skillseed.auth.dto;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String email,
        String fullName,
        boolean verified,
        short verificationLevel,
        boolean onboardingCompleted,
        String authProvider) {
}