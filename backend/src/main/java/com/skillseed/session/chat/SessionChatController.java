package com.skillseed.session.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

/**
 * STOMP {@code @MessageMapping} handler for in-session chat.
 *
 * <p>Clients send to {@code /app/sessions/{bookingId}/chat} and
 * receive broadcasts on {@code /topic/sessions/{bookingId}}.
 *
 * <p>After T-M412 the controller is a pure dispatcher: participant
 * authorisation and sender-name resolution live in
 * {@link SessionChatService}.
 */
@Controller
public class SessionChatController {

    private static final Logger log = LoggerFactory.getLogger(SessionChatController.class);

    private final SessionChatService sessionChatService;

    public SessionChatController(SessionChatService sessionChatService) {
        this.sessionChatService = sessionChatService;
    }

    @MessageMapping("/sessions/{bookingId}/chat")
    @SendTo("/topic/sessions/{bookingId}")
    public SessionChatMessage handle(@DestinationVariable UUID bookingId,
                                     SessionChatMessage incoming,
                                     Principal principal) {
        if (incoming == null || incoming.getBody() == null || incoming.getBody().isBlank()) {
            return null;
        }
        UUID senderId = resolveSenderId(principal, incoming);
        if (senderId == null) {
            log.warn("Chat message without principal on booking {}", bookingId);
            return null;
        }

        var ctx = sessionChatService.resolveChatContext(bookingId, senderId);
        if (ctx.isEmpty()) {
            // Non-participant — silently drop (preserve original behaviour).
            return null;
        }

        incoming.setBookingId(bookingId);
        incoming.setSenderId(senderId);
        incoming.setSenderName(ctx.get().senderName());
        incoming.setSentAt(Instant.now());
        return incoming;
    }

    private static UUID resolveSenderId(Principal principal, SessionChatMessage incoming) {
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
