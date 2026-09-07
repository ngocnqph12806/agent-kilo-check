package com.skillseed.user.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response for the wanted-skills CRUD endpoints.
 */
public record WantedSkillResponse(
        UUID id,
        UUID skillId,
        String skillSlug,
        String skillName,
        String category,
        short priority,
        Short targetLevel,
        String notes,
        Instant createdAt) {
}