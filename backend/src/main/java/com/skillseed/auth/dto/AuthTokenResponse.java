package com.skillseed.auth.dto;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        String tokenType,
        UserSummaryResponse user) {
}
