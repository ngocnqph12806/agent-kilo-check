package com.skillseed.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request body for {@code PATCH /api/v1/users/me/skills/offered/{id}}.
 * All fields optional; null = leave unchanged.
 */
public record UpdateOfferedSkillRequest(
        @Min(value = 1, message = "level must be between 1 and 5")
        @Max(value = 5, message = "level must be between 1 and 5")
        Short level,

        @Positive(message = "yearsExperience must be positive")
        Integer yearsExperience,

        @Size(max = 1000, message = "description must be at most 1000 characters")
        String description,

        @Positive(message = "hourlySeedRate must be positive")
        Integer hourlySeedRate,

        Boolean active) {
}