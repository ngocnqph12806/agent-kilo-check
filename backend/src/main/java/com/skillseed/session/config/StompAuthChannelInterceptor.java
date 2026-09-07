package com.skillseed.session.config;

import com.skillseed.auth.service.JwtService;
import com.skillseed.shared.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Bridges a JWT access token (sent on the STOMP {@code CONNECT}
 * frame) onto the WebSocket {@code Principal}. The HTTP filter chain
 * does not run for WebSocket upgrades, so the handshake must be
 * authenticated here.
 *
 * <p>If the token is missing or invalid the interceptor attaches an
 * {@code anonymous} principal. The
 * {@link com.skillseed.session.chat.SessionChatController} then
 * refuses to broadcast messages from anyone other than the two
 * participants of the booking.
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(StompAuthChannelInterceptor.class);

    private final JwtService jwtService;

    public StompAuthChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization");
            UUID userId = parseUserId(auth);
            if (userId != null) {
                accessor.setUser(new StompPrincipal(userId.toString()));
            } else {
                accessor.setUser(new StompPrincipal("anonymous"));
            }
        } else if (accessor.getUser() == null) {
            accessor.setUser(new StompPrincipal("anonymous"));
        }
        return message;
    }

    private UUID parseUserId(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        try {
            Claims claims = jwtService.parseAndValidate(
                    header.substring("Bearer ".length()).trim(), "access");
            return UUID.fromString(claims.getSubject());
        } catch (Exception ex) {
            log.debug("STOMP connect with invalid JWT: {}", ex.getMessage());
            return null;
        }
    }

    public record StompPrincipal(String name) implements java.security.Principal {
        @Override
        public String getName() {
            return name;
        }

        public UUID userId() {
            try {
                return UUID.fromString(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
