package com.skillseed.user.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * GDPR data export payload (T-M201). Aggregates every piece of personal
 * data we hold for the requesting user so they can download it as a
 * single JSON document via {@code GET /api/v1/users/me/export}.
 */
public record UserDataExportResponse(
        Instant exportedAt,
        ProfileSection profile,
        List<OfferedSkillSection> offeredSkills,
        List<WantedSkillSection> wantedSkills,
        List<AvailabilitySection> availability,
        WalletSection wallet,
        List<SeedTransactionSection> seedTransactions,
        List<BookingSection> bookingsAsTeacher,
        List<BookingSection> bookingsAsLearner,
        List<RatingSection> ratingsGiven,
        List<RatingSection> ratingsReceived) {

    public record ProfileSection(
            UUID id,
            String email,
            String phone,
            String fullName,
            String avatarUrl,
            String bio,
            String countryCode,
            String timezone,
            List<String> languages,
            String learningStyle,
            String authProvider,
            boolean verified,
            Instant createdAt,
            Instant updatedAt) {
    }

    public record OfferedSkillSection(
            String skillSlug,
            String skillName,
            String category,
            String level,
            Integer yearsExperience,
            String description,
            Integer hourlySeedRate) {
    }

    public record WantedSkillSection(
            String skillSlug,
            String skillName,
            String category,
            String level,
            String description) {
    }

    public record AvailabilitySection(
            String timezone,
            int dayOfWeek,
            String startTime,
            String endTime) {
    }

    public record WalletSection(
            int balance,
            int totalEarned,
            int totalSpent) {
    }

    public record SeedTransactionSection(
            String type,
            int amount,
            int balanceAfter,
            String description,
            String status,
            Instant createdAt,
            Instant expiresAt) {
    }

    public record BookingSection(
            UUID id,
            UUID otherPartyId,
            String otherPartyName,
            String status,
            Instant scheduledAt,
            int durationMinutes,
            Instant createdAt) {
    }

    public record RatingSection(
            UUID bookingId,
            UUID raterId,
            UUID rateeId,
            Integer overallScore,
            String comment,
            boolean autoRated,
            Instant createdAt) {
    }
}
