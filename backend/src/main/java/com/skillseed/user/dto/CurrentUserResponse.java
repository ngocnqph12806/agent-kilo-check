package com.skillseed.user.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response shape of {@code GET /api/v1/users/me}: the authenticated
 * user's profile plus a lightweight Skill DNA summary.
 */
public record CurrentUserResponse(
        UUID id,
        String email,
        String fullName,
        String avatarUrl,
        String bio,
        String countryCode,
        String timezone,
        List<String> languages,
        String learningStyle,
        String authProvider,
        boolean verified,
        short verificationLevel,
        boolean onboardingCompleted,
        BigDecimal ratingAvg,
        int sessionsCompleted,
        Instant createdAt,
        SkillDnaSummary skillDna,
        WalletSummary wallet) {

    public record SkillDnaSummary(
            List<OfferedSkill> offered,
            int wantedCount) {
    }

    public record OfferedSkill(
            UUID id,
            UUID skillId,
            String skillSlug,
            String skillName,
            String category,
            short level,
            Integer yearsExperience,
            String description,
            int hourlySeedRate) {
    }

    public record WalletSummary(
            int balance,
            int totalEarned,
            int totalSpent) {
    }
}