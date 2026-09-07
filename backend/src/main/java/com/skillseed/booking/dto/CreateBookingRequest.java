package com.skillseed.booking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID teacherId,
        @NotNull UUID skillId,
        @NotNull Instant scheduledAt,
        @NotNull @Min(15) @Max(60) Integer durationMinutes,
        @Size(max = 500) String notes) {
}