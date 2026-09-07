package com.skillseed.skill.dto;

import java.util.UUID;

/**
 * Lightweight skill summary returned by search / list endpoints.
 */
public record SkillResponse(
        UUID id,
        String slug,
        String name,
        String category,
        boolean custom,
        String status,
        UUID parentId) {
}