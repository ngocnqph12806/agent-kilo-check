package com.skillseed.user.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response for the offered-skills CRUD endpoints.
 */
public record OfferedSkillResponse(
        UUID id,
        UUID skillId,
        String skillSlug,
        String skillName,
        String category,
        short level,
        Integer yearsExperience,
        String description,
        int hourlySeedRate,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}