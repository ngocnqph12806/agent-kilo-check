package com.skillseed.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for {@code PATCH /api/v1/users/me}. All fields are
 * optional; null values leave the corresponding column untouched.
 */
public record UpdateProfileRequest(
        @Size(max = 255, message = "fullName must be at most 255 characters")
        String fullName,

        @Size(max = 500, message = "bio must be at most 500 characters")
        String bio,

        @Size(min = 2, max = 2, message = "countryCode must be a 2-letter ISO code")
        @Pattern(regexp = "^[A-Z]{2}$", message = "countryCode must be uppercase ISO 3166-1 alpha-2")
        String countryCode,

        @Size(max = 50, message = "timezone must be at most 50 characters")
        String timezone,

        List<@Size(min = 2, max = 10) String> languages,

        @Size(max = 20, message = "learningStyle must be at most 20 characters")
        String learningStyle) {
}