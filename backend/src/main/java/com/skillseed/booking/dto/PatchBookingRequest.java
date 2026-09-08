package com.skillseed.booking.dto;

import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Partial update for a booking — reschedule or notes only. Per spec
 * §8.1 (audit #20). All fields are optional; only provided fields
 * are applied. Only valid on PENDING or CONFIRMED bookings.
 */
public record PatchBookingRequest(
        Instant scheduledAt,
        @Size(max = 1000) String notes,
        Integer durationMinutes) {
}
