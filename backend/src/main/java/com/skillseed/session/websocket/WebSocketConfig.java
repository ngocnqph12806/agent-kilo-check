package com.skillseed.session.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket / STOMP wiring (T-M155).
 *
 * <p>Endpoint: {@code /ws/sessions/{bookingId}} — clients subscribe to
 * {@code /topic/sessions/{bookingId}} and publish on
 * {@code /app/sessions/{bookingId}/chat}.
 *
 * <p>Authentication is delegated to {@link org.springframework.web.socket.WebSocketHandlerDecoratorFactory}
 * if/when we add JWT — for the MVP we authenticate the user inside the
 * controller by parsing the {@code Authorization} header on the initial
 * HTTP upgrade request (see {@code ChatHandshakeInterceptor}).
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatHandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(ChatHandshakeInterceptor handshakeInterceptor) {
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/sessions")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
