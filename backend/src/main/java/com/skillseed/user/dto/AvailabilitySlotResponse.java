package com.skillseed.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Single weekly availability slot as returned by
 * {@code GET /api/v1/users/me/availability}.
 */
public record AvailabilitySlotResponse(
        UUID id,
        short dayOfWeek,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime startTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime endTime,
        String timezone) {
}