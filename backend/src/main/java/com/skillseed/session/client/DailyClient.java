package com.skillseed.session.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillseed.session.exception.SessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Thin Daily.co REST client covering the two calls we need for the MVP:
 * <ul>
 *   <li>{@code POST /rooms} — create or fetch a room for a booking.</li>
 *   <li>{@code POST /meeting-tokens} — mint a meeting token for a participant.</li>
 * </ul>
 *
 * <p>Failures bubble up as {@link SessionException} so the session
 * controller can render a uniform error response.
 */
@Component
public class DailyClient {

    private static final Logger log = LoggerFactory.getLogger(DailyClient.class);

    private final DailyProperties props;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DailyClient(DailyProperties props,
                       RestTemplateBuilder builder,
                       ObjectMapper objectMapper) {
        this.props = props;
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = objectMapper;
    }

    public boolean isConfigured() {
        return props.getApiKey() != null && !props.getApiKey().isBlank();
    }

    /**
     * Create a private room for the given booking. {@code exp} is the
     * absolute epoch seconds when the room should stop accepting new joins.
     */
    public DailyDtos.RoomResponse createRoom(String roomName, Instant expiresAt) {
        ensureConfigured();
        DailyDtos.CreateRoomRequest body = new DailyDtos.CreateRoomRequest(roomName, expiresAt);
        ResponseEntity<DailyDtos.RoomResponse> response = exchange(
                HttpMethod.POST, "/rooms", body, DailyDtos.RoomResponse.class);
        return response.getBody();
    }

    /**
     * Mint a meeting token for a single participant. The {@code userId}
     * lets Daily surface the participant name in their client and is
     * stable across reconnects.
     */
    public String createMeetingToken(String userName, String userId,
                                     boolean isOwner, Instant expiresAt) {
        ensureConfigured();
        DailyDtos.CreateMeetingTokenRequest body = new DailyDtos.CreateMeetingTokenRequest(
                userName, userId, isOwner, expiresAt);
        ResponseEntity<DailyDtos.MeetingTokenResponse> response = exchange(
                HttpMethod.POST, "/meeting-tokens", body, DailyDtos.MeetingTokenResponse.class);
        DailyDtos.MeetingTokenResponse token = response.getBody();
        if (token == null || token.getToken() == null || token.getToken().isBlank()) {
            throw SessionException.internal("DAILY_EMPTY_TOKEN", "Daily returned an empty meeting token");
        }
        return token.getToken();
    }

    private void ensureConfigured() {
        if (!isConfigured()) {
            throw SessionException.unprocessable("DAILY_DISABLED",
                    "Daily.co integration is not configured (DAILY_API_KEY missing)");
        }
    }

    private <T> ResponseEntity<T> exchange(HttpMethod method, String path, Object body, Class<T> type) {
        URI uri = URI.create(props.getApiBase() + path);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.getApiKey());
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(uri, method, entity, type);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String detail = Optional.ofNullable(ex.getResponseBodyAsString())
                    .map(this::extractDailyError)
                    .orElse(ex.getStatusText());
            log.warn("Daily.co {} {} failed: {} {}", method, path, ex.getStatusCode(), detail);
            throw SessionException.internal("DAILY_API_ERROR",
                    "Daily.co API error: " + detail);
        } catch (RuntimeException ex) {
            log.warn("Daily.co {} {} failed: {}", method, path, ex.getMessage());
            throw SessionException.internal("DAILY_UNAVAILABLE",
                    "Daily.co API unreachable: " + ex.getMessage());
        }
    }

    private String extractDailyError(String body) {
        try {
            DailyDtos.DailyError err = objectMapper.readValue(body, DailyDtos.DailyError.class);
            if (err != null && err.getError() != null) {
                return err.getError();
            }
        } catch (Exception ignored) {
        }
        return body;
    }
}
