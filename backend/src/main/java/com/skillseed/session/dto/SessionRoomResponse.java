package com.skillseed.session.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response shape for {@code POST /api/v1/sessions/{bookingId}/room}.
 *
 * <p>{@code token} is the meeting token for the calling participant; the
 * other party receives their own via the same endpoint. We always
 * include {@code role} so the frontend can hint to the user whether
 * they are the owner of the room.
 */
public record SessionRoomResponse(
        UUID bookingId,
        String roomUrl,
        String roomName,
        String token,
        String role,
        Instant expiresAt,
        Instant scheduledAt,
        int durationMinutes) {
}
