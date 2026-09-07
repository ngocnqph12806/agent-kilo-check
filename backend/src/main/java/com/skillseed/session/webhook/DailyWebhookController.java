package com.skillseed.session.webhook;

import com.skillseed.session.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Daily.co webhook receiver (T-M156). Public endpoint — Daily calls us
 * with {@code meeting.ended} once the room is closed. We translate it
 * to a booking status transition.
 *
 * <p>Mapping {@code room_name} → {@code bookingId} uses the
 * {@code ss-<bookingIdCompact>} scheme that {@code SessionService}
 * uses when creating rooms, so no extra storage is needed.
 */
@RestController
@RequestMapping("/api/v1/webhooks/daily")
public class DailyWebhookController {

    private static final Logger log = LoggerFactory.getLogger(DailyWebhookController.class);

    private final SessionService sessionService;

    public DailyWebhookController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> handle(@RequestBody DailyWebhookPayload.Envelope envelope) {
        String eventType = resolveEventType(envelope);
        if (eventType == null) {
            return ResponseEntity.ok(Map.of("status", "ignored"));
        }
        if (!"meeting.ended".equalsIgnoreCase(eventType)) {
            log.debug("Ignoring Daily event {}", eventType);
            return ResponseEntity.ok(Map.of("status", "ignored", "event", eventType));
        }
        Optional<UUID> bookingId = resolveBookingId(envelope);
        if (bookingId.isEmpty()) {
            log.warn("Daily meeting.ended with no resolvable booking (room={})",
                    envelope == null ? null : envelope.getEvent() == null ? null : envelope.getEvent().getRoomName());
            return ResponseEntity.ok(Map.of("status", "unknown_booking"));
        }
        sessionService.markMeetingEnded(bookingId.get());
        return ResponseEntity.ok(Map.of("status", "processed", "event", eventType));
    }

    private String resolveEventType(DailyWebhookPayload.Envelope envelope) {
        if (envelope == null) {
            return null;
        }
        if (envelope.getType() != null && !envelope.getType().isBlank()) {
            return envelope.getType();
        }
        return envelope.getEvent() == null ? null : envelope.getEvent().getType();
    }

    private Optional<UUID> resolveBookingId(DailyWebhookPayload.Envelope envelope) {
        if (envelope == null) {
            return Optional.empty();
        }
        String roomName = null;
        if (envelope.getEvent() != null) {
            roomName = envelope.getEvent().getRoomName() != null
                    ? envelope.getEvent().getRoomName()
                    : envelope.getEvent().getRoom();
        }
        UUID bookingIdFromRoom = parseBookingIdFromRoomName(roomName);
        return bookingIdFromRoom == null ? Optional.empty() : Optional.of(bookingIdFromRoom);
    }

    private UUID parseBookingIdFromRoomName(String roomName) {
        if (roomName == null || !roomName.startsWith("ss-")) {
            return null;
        }
        String compact = roomName.substring(3);
        if (compact.length() < 32) {
            return null;
        }
        try {
            String uuid = compact.substring(0, 8) + "-"
                    + compact.substring(8, 12) + "-"
                    + compact.substring(12, 16) + "-"
                    + compact.substring(16, 20) + "-"
                    + compact.substring(20, 32);
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
