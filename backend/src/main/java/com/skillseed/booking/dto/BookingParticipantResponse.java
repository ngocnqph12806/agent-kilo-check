package com.skillseed.booking.dto;

import java.util.UUID;

public record BookingParticipantResponse(
        UUID id,
        String fullName,
        String avatarUrl) {
}