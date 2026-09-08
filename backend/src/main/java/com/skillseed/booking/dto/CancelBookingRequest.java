package com.skillseed.booking.dto;

import com.skillseed.booking.domain.CancelReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelBookingRequest(
        @NotNull CancelReason reason,
        @Size(max = 500) String message) {
}