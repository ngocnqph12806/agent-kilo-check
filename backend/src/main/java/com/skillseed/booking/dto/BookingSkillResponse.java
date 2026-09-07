package com.skillseed.booking.dto;

import java.util.UUID;

public record BookingSkillResponse(
        UUID id,
        String name,
        String slug,
        String category) {
}