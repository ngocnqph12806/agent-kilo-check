package com.skillseed.session.chat;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.session.exception.SessionException;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.UUID;

/**
 * STOMP {@code @MessageMapping} handler for in-session chat.
 *
 * <p>Clients send to {@code /app/sessions/{bookingId}/chat} and
 * receive broadcasts on {@code /topic/sessions/{bookingId}}.
 *
 * <p>Authorisation is enforced inside the handler: only teacher
 * or learner on the booking may publish; everyone else gets an
 * empty broadcast (so the message is silently dropped rather than
 * pushed to a forbidden topic).
 */
@Controller
public class SessionChatController {

    private static final Logger log = LoggerFactory.getLogger(SessionChatController.class);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public SessionChatController(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/sessions/{bookingId}/chat")
    @SendTo("/topic/sessions/{bookingId}")
    public SessionChatMessage handle(@DestinationVariable UUID bookingId,
                                     SessionChatMessage incoming,
                                     java.security.Principal principal) {
        if (incoming == null || incoming.getBody() == null || incoming.getBody().isBlank()) {
            return null;
        }
        UUID senderId = resolveSenderId(principal, incoming);
        if (senderId == null) {
            log.warn("Chat message without principal on booking {}", bookingId);
            return null;
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw SessionException.notFound("BOOKING_NOT_FOUND",
                    "Booking " + bookingId + " not found");
        }
        UUID teacherId = booking.getTeacher().getId();
        UUID learnerId = booking.getLearner().getId();
        if (!senderId.equals(teacherId) && !senderId.equals(learnerId)) {
            log.warn("Non-participant {} attempted to chat on booking {}", senderId, bookingId);
            return null;
        }

        User sender = userRepository.findById(senderId).orElse(null);
        String name = sender != null ? sender.getFullName() : "Anonymous";

        incoming.setBookingId(bookingId);
        incoming.setSenderId(senderId);
        incoming.setSenderName(name);
        incoming.setSentAt(Instant.now());
        return incoming;
    }

    private static UUID resolveSenderId(java.security.Principal principal,
                                        SessionChatMessage incoming) {
        if (incoming != null && incoming.getSenderId() != null) {
            return incoming.getSenderId();
        }
        if (principal == null || principal.getName() == null) {
            return null;
        }
        try {
            return UUID.fromString(principal.getName());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
