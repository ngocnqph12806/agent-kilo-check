package com.skillseed.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record OAuthGoogleRequest(
        @NotBlank(message = "idToken is required")
        String idToken) {
}