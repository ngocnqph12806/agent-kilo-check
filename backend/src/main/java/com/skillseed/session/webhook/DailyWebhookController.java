package com.skillseed.session.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillseed.session.client.DailyProperties;
import com.skillseed.session.security.DailyWebhookSignatureVerifier;
import com.skillseed.session.service.SessionService;
import com.skillseed.shared.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Inbound webhook endpoint for Daily.co. Listens for
 * {@code meeting.ended} events and asks
 * {@link SessionService#markMeetingEnded(String)} to
 * flip the corresponding booking to {@code COMPLETED}.
 *
 * <p>Marked public in {@code SecurityConfig.PUBLIC_PATHS}
 * because Daily cannot authenticate with our JWT issuer.
 * Defense against spoofed payloads: HMAC-SHA256 verification
 * of the raw body against the
 * {@code session.daily.webhook-signing-key} configuration
 * property, compared in constant time.
 *
 * <p><b>Fail-closed:</b> if the signing key is unset the
 * signature check rejects every request (401). The previous
 * behaviour of skipping the check and logging a warning was a
 * spoofing risk because the endpoint is publicly reachable —
 * anyone who knew the room-name convention
 * ({@code ss-<uuid>}) could trigger {@code markMeetingEnded}
 * and credit a teacher's escrow.
 */
@RestController
@RequestMapping("/api/v1/webhooks/daily")
public class DailyWebhookController {

    private static final Logger log = LoggerFactory.getLogger(DailyWebhookController.class);
    private static final String SIGNATURE_HEADER = "X-Daily-Signature";

    private final SessionService sessionService;
    private final DailyProperties dailyProperties;
    private final ObjectMapper objectMapper;

    public DailyWebhookController(SessionService sessionService,
                                  DailyProperties dailyProperties,
                                  ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.dailyProperties = dailyProperties;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> handle(HttpServletRequest httpRequest) {
        byte[] raw;
        DailyWebhookPayload body;
        try {
            raw = httpRequest.getInputStream().readAllBytes();
            body = objectMapper.readValue(raw, DailyWebhookPayload.class);
        } catch (IOException ex) {
            log.warn("Failed to read Daily webhook body: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
        if (body == null || body.getType() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!verifySignature(raw, httpRequest)) {
            log.warn("Rejected Daily webhook with invalid/missing signature");
            return unauthorized();
        }
        if (!"meeting.ended".equals(body.getType())) {
            log.debug("Ignoring Daily webhook event type '{}'", body.getType());
            return ResponseEntity.noContent().build();
        }
        String roomName = body.getPayload() != null && body.getPayload().getRoom() != null
                ? body.getPayload().getRoom().getName() : null;
        if (roomName == null) {
            log.warn("Daily meeting.ended payload missing room.name");
            return ResponseEntity.badRequest().build();
        }
        log.info("Daily meeting.ended received for room {}", roomName);
        sessionService.markMeetingEnded(roomName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private boolean verifySignature(byte[] rawBody, HttpServletRequest httpRequest) {
        String key = dailyProperties.getWebhookSigningKey();
        if (key == null || key.isBlank()) {
            // Fail-closed: rejecting every request is safer than
            // letting unsigned payloads through on a public endpoint.
            // Operators see this in logs and fix the configuration.
            log.error("Daily webhook signing key is not configured; rejecting all webhooks");
            return false;
        }
        String signature = httpRequest.getHeader(SIGNATURE_HEADER);
        return DailyWebhookSignatureVerifier.verify(rawBody, signature, key);
    }

    private ResponseEntity<ApiErrorResponse> unauthorized() {
        ApiErrorResponse payload = ApiErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "WEBHOOK_SIGNATURE_INVALID",
                "Webhook signature is missing or invalid");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload);
    }
}
