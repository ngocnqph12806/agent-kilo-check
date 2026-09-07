package com.skillseed.session.chat;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.session.exception.SessionException;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.session.websocket.ChatHandshakeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * STOMP chat controller (T-M155).
 *
 * <p>Path: {@code /app/sessions/{bookingId}/chat} → broadcast on
 * {@code /topic/sessions/{bookingId}}.
 *
 * <p>The user's identity is read from the handshake attributes populated
 * by {@link ChatHandshakeInterceptor}; we additionally verify they're a
 * participant of the booking before echoing their message to the room.
 */
@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final BookingRepository bookingRepository;

    public ChatController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @MessageMapping("/sessions/{bookingId}/chat")
    @SendTo("/topic/sessions/{bookingId}")
    public Map<String, Object> chat(@DestinationVariable UUID bookingId,
                                    @Payload ChatMessage incoming,
                                    SimpMessageHeaderAccessor headers) {
        String userId = Optional.ofNullable(
                        (String) headers.getSessionAttributes().get(ChatHandshakeInterceptor.ATTR_USER_ID))
                .orElseThrow(() -> new SessionException.forbidden("UNAUTHENTICATED",
                        "WebSocket session has no authenticated user"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> SessionException.notFound("BOOKING_NOT_FOUND",
                        "Booking not found"));

        if (!userId.equals(booking.getTeacher().getId().toString())
                && !userId.equals(booking.getLearner().getId().toString())) {
            throw SessionException.forbidden("NOT_PARTICIPANT",
                    "Only participants can post in this chat");
        }

        BookingStatus status = booking.getStatus();
        if (status != BookingStatus.CONFIRMED
                && status != BookingStatus.IN_PROGRESS) {
            throw SessionException.conflict("INVALID_STATE_TRANSITION",
                    "Chat is closed for booking in state " + status.getDbValue());
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("bookingId", bookingId.toString());
        out.put("userId", userId);
        out.put("text", incoming == null || incoming.text() == null ? "" : incoming.text());
        out.put("sentAt", Instant.now().toString());
        return out;
    }

    public record ChatMessage(String text) {
    }
}
