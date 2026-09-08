package com.skillseed.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

/**
 * A single recurring weekly availability slot. {@code dayOfWeek} is
 * 0=Sunday … 6=Saturday.
 */
public record AvailabilitySlotRequest(
        @NotNull(message = "dayOfWeek is required")
        @Min(value = 0, message = "dayOfWeek must be 0..6")
        @Max(value = 6, message = "dayOfWeek must be 0..6")
        Short dayOfWeek,

        @NotNull(message = "startTime is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "endTime is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime endTime,

        @NotBlank(message = "timezone is required")
        @Size(max = 50, message = "timezone must be at most 50 characters")
        String timezone) {
}