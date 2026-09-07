package com.skillseed.booking.dto;

import com.skillseed.booking.domain.Booking;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        String status,
        BookingParticipantResponse teacher,
        BookingParticipantResponse learner,
        BookingSkillResponse skill,
        Instant scheduledAt,
        int durationMinutes,
        int seedAmount,
        String meetingUrl,
        String recordingUrl,
        String notes,
        String cancellationReason,
        UUID cancelledBy,
        Instant createdAt,
        Instant updatedAt) {

    public static BookingResponse from(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getStatus().getDbValue(),
                new BookingParticipantResponse(
                        b.getTeacher().getId(),
                        b.getTeacher().getFullName(),
                        b.getTeacher().getAvatarUrl()),
                new BookingParticipantResponse(
                        b.getLearner().getId(),
                        b.getLearner().getFullName(),
                        b.getLearner().getAvatarUrl()),
                new BookingSkillResponse(
                        b.getSkill().getId(),
                        b.getSkill().getName(),
                        b.getSkill().getSlug(),
                        b.getSkill().getCategory().getDbValue()),
                b.getScheduledAt(),
                b.getDurationMinutes(),
                b.getSeedAmount(),
                b.getMeetingUrl(),
                b.getRecordingUrl(),
                b.getNotes(),
                b.getCancellationReason(),
                b.getCancelledBy() == null ? null : b.getCancelledBy().getId(),
                b.getCreatedAt(),
                b.getUpdatedAt());
    }
}