package com.skillseed.session.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Thin REST client for the Daily.co REST API used to
 * create rooms and meeting tokens. When the integration
 * is disabled (no API key in the current profile), the
 * client returns deterministic placeholder URLs/tokens
 * so the booking flow can be exercised end-to-end on a
 * developer laptop.
 */
@Component
public class DailyClient {

    private static final Logger log = LoggerFactory.getLogger(DailyClient.class);

    private final DailyProperties properties;
    private final RestClient http;
    private final ObjectMapper objectMapper;

    public DailyClient(DailyProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.http = RestClient.builder()
                .baseUrl(properties.getApiBase())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Create (or look up) a Daily room with the given name and expiry.
     * If the room already exists Daily returns 409 — we treat that as
     * success and re-fetch via {@link #getRoom(String)}.
     */
    public DailyDtos.CreateRoomResponse createRoom(String name, long expSeconds) {
        if (!properties.isEnabled()) {
            DailyDtos.CreateRoomResponse stub = new DailyDtos.CreateRoomResponse();
            stub.setName(name);
            stub.setUrl("https://stub.daily.co/" + name);
            stub.setPrivacy("private");
            return stub;
        }
        DailyDtos.RoomProperties props =
                new DailyDtos.RoomProperties(expSeconds, true, true);
        DailyDtos.CreateRoomRequest req = new DailyDtos.CreateRoomRequest(name, props);
        try {
            return http.post()
                    .uri("/rooms")
                    .body(req)
                    .retrieve()
                    .body(DailyDtos.CreateRoomResponse.class);
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status.value() == 409) {
                log.info("Daily room {} already exists, fetching existing record", name);
                return getRoom(name);
            }
            throw e;
        }
    }

    public DailyDtos.CreateRoomResponse getRoom(String name) {
        if (!properties.isEnabled()) {
            DailyDtos.CreateRoomResponse stub = new DailyDtos.CreateRoomResponse();
            stub.setName(name);
            stub.setUrl("https://stub.daily.co/" + name);
            stub.setPrivacy("private");
            return stub;
        }
        return http.get()
                .uri("/rooms/{name}", name)
                .retrieve()
                .body(DailyDtos.CreateRoomResponse.class);
    }

    /**
     * Mint a meeting token scoped to {@code roomName} for the given
     * participant. {@code owner=true} promotes the caller to room
     * moderator (teacher by default).
     */
    public String createMeetingToken(String roomName, String userId, String userName,
                                     boolean owner, long expSeconds) {
        if (!properties.isEnabled()) {
            return "stub-token-" + userId + "-" + roomName;
        }
        DailyDtos.MeetingTokenProperties props =
                new DailyDtos.MeetingTokenProperties(roomName, userId, userName, owner, expSeconds);
        DailyDtos.CreateMeetingTokenRequest req =
                new DailyDtos.CreateMeetingTokenRequest(props);
        DailyDtos.CreateMeetingTokenResponse resp = http.post()
                .uri("/meeting-tokens")
                .body(req)
                .retrieve()
                .body(DailyDtos.CreateMeetingTokenResponse.class);
        if (resp == null || resp.getToken() == null) {
            throw new IllegalStateException("Daily returned no meeting token");
        }
        return resp.getToken();
    }
}
