package com.skillseed.skill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request body for {@code POST /api/v1/skills} (user-created custom skill).
 * Always creates a {@code is_custom=true} row in {@code PENDING_REVIEW}.
 */
public record CreateCustomSkillRequest(
        @NotBlank(message = "name is required")
        @Size(max = 255, message = "name must be at most 255 characters")
        String name,

        @NotBlank(message = "category is required")
        @Pattern(regexp = "^(tech|business|art|language|life|health|music|sport)$",
                message = "category must be one of: tech, business, art, language, life, health, music, sport")
        String category,

        UUID parentId) {
}