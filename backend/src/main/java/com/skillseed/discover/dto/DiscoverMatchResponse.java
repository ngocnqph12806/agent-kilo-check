package com.skillseed.discover.dto;

import java.util.List;
import java.util.UUID;

/**
 * Single result row from {@code GET /api/v1/discover}: the minimum
 * surface the card grid needs to render a match.
 */
public record DiscoverMatchResponse(
        UUID userId,
        String fullName,
        String avatarUrl,
        String countryCode,
        List<String> languages,
        String bio,
        java.math.BigDecimal ratingAvg,
        int sessionsCompleted,
        int matchedSkillCount,
        List<MatchedSkill> topSkills) {

    public record MatchedSkill(
            UUID skillId,
            String slug,
            String name,
            String category,
            short level,
            int hourlySeedRate) {
    }
}
