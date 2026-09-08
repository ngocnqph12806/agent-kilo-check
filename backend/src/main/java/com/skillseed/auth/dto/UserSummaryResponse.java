package com.skillseed.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserSummaryResponse(
        UUID id,
        String email,
        String fullName,
        String avatarUrl,
        boolean verified,
        short verificationLevel,
        boolean onboardingCompleted,
        String authProvider,
        String countryCode,
        String timezone) {
}
