package com.skillseed.booking.dto;

import com.skillseed.booking.domain.Booking;

import java.time.Instant;
import java.util.UUID;

public record BookingSummaryResponse(
        UUID id,
        String status,
        UUID teacherId,
        String teacherName,
        UUID learnerId,
        String learnerName,
        UUID skillId,
        String skillName,
        Instant scheduledAt,
        int durationMinutes,
        int seedAmount,
        Instant createdAt) {

    public static BookingSummaryResponse from(Booking b) {
        return new BookingSummaryResponse(
                b.getId(),
                b.getStatus().getDbValue(),
                b.getTeacher().getId(),
                b.getTeacher().getFullName(),
                b.getLearner().getId(),
                b.getLearner().getFullName(),
                b.getSkill().getId(),
                b.getSkill().getName(),
                b.getScheduledAt(),
                b.getDurationMinutes(),
                b.getSeedAmount(),
                b.getCreatedAt());
    }
}