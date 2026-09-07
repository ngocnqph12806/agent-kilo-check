package com.skillseed.session.websocket;

import com.skillseed.auth.service.JwtService;
import com.skillseed.shared.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Parses the JWT from the upgrade request and stores the
 * {@link AuthenticatedUser} in the WebSocket session attributes so the
 * STOMP controller can authorise chat messages.
 */
@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ChatHandshakeInterceptor.class);
    public static final String ATTR_USER_ID = "skillseed.userId";
    public static final String ATTR_EMAIL = "skillseed.email";

    private final JwtService jwtService;

    public ChatHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        String token = extractBearer(request);
        if (token.isEmpty()) {
            log.debug("Rejecting WS handshake — no bearer token");
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }
        try {
            Claims claims = jwtService.parseAndValidate(token, "access");
            UUID userId = UUID.fromString(claims.getSubject());
            String email = claims.get("email", String.class);
            short level = ((Number) claims.get("verificationLevel")).shortValue();
            boolean verified = level > 0;
            AuthenticatedUser user = new AuthenticatedUser(userId, email, level, verified);
            attributes.put(ATTR_USER_ID, user.getUserId().toString());
            attributes.put(ATTR_EMAIL, user.getEmail());
            return true;
        } catch (RuntimeException ex) {
            log.debug("Rejecting WS handshake — invalid token: {}", ex.getMessage());
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               @Nullable Exception exception) {
    }

    private String extractBearer(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String header = servletRequest.getServletRequest().getHeader(HttpHeaders.AUTHORIZATION);
            return Optional.ofNullable(header)
                    .filter(h -> h.startsWith("Bearer "))
                    .map(h -> h.substring("Bearer ".length()).trim())
                    .orElse("");
        }
        return Optional.ofNullable(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring("Bearer ".length()).trim())
                .orElse("");
    }
}
