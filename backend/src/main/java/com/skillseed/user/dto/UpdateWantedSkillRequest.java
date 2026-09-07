package com.skillseed.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request body for {@code PATCH /api/v1/users/me/skills/wanted/{id}}.
 * All fields optional; null = leave unchanged.
 */
public record UpdateWantedSkillRequest(
        @Min(value = 1, message = "priority must be between 1 and 5")
        @Max(value = 5, message = "priority must be between 1 and 5")
        Short priority,

        @Min(value = 1, message = "targetLevel must be between 1 and 5")
        @Max(value = 5, message = "targetLevel must be between 1 and 5")
        Short targetLevel,

        @Size(max = 1000, message = "notes must be at most 1000 characters")
        String notes) {
}