package com.skillseed.user.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Public profile view returned by {@code GET /api/v1/users/{id}}.
 * Email and phone are intentionally omitted.
 */
public record PublicUserResponse(
        UUID id,
        String fullName,
        String avatarUrl,
        String bio,
        String countryCode,
        String timezone,
        List<String> languages,
        String learningStyle,
        boolean verified,
        short verificationLevel,
        boolean onboardingCompleted,
        BigDecimal ratingAvg,
        int sessionsCompleted,
        Instant createdAt,
        List<OfferedSkill> offeredSkills) {

    public record OfferedSkill(
            UUID id,
            UUID skillId,
            String skillSlug,
            String skillName,
            String category,
            short level,
            Integer yearsExperience,
            int hourlySeedRate) {
    }
}