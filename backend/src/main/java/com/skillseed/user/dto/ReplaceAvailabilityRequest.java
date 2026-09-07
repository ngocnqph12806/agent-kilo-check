package com.skillseed.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for {@code PUT /api/v1/users/me/availability}: the full
 * set of weekly slots that replaces whatever the user previously saved.
 */
public record ReplaceAvailabilityRequest(
        @NotNull(message = "slots is required")
        @Size(max = 50, message = "at most 50 slots per user")
        @Valid
        List<AvailabilitySlotRequest> slots) {
}