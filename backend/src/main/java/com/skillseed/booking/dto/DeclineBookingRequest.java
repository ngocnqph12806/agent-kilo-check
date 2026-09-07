package com.skillseed.booking.dto;

import jakarta.validation.constraints.Size;

public record DeclineBookingRequest(
        @Size(max = 500) String reason) {
}