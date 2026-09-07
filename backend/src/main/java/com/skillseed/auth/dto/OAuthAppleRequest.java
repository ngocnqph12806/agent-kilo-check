package com.skillseed.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record OAuthAppleRequest(
        @NotBlank(message = "idToken is required")
        String idToken,

        String fullName) {
}