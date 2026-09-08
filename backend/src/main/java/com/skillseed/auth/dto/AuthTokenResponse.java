package com.skillseed.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        @JsonProperty("expiresIn") long expiresInSeconds,
        String tokenType,
        UserSummaryResponse user,
        @JsonIgnore boolean secureCookie) {

    public AuthTokenResponse(String accessToken,
                             String refreshToken,
                             long expiresInSeconds,
                             String tokenType,
                             UserSummaryResponse user) {
        this(accessToken, refreshToken, expiresInSeconds, tokenType, user, false);
    }
}
