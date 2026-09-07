package com.skillseed.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelBookingRequest(
        @NotBlank @Size(max = 50) String reason,
        @Size(max = 500) String message) {
}