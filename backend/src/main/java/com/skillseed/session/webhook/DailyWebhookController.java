package com.skillseed.session.webhook;

import com.skillseed.session.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound webhook endpoint for Daily.co. Listens for
 * {@code meeting.ended} events and asks
 * {@link SessionService#markMeetingEnded(String)} to
 * flip the corresponding booking to {@code COMPLETED}.
 *
 * <p>Marked public in {@code SecurityConfig.PUBLIC_PATHS}
 * because Daily cannot authenticate with our JWT issuer.
 * The route returns 204 in every case except for a
 * malformed payload (400) so that Daily does not retry
 * noisy event types we do not handle.
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
    public ResponseEntity<Void> handle(@RequestBody DailyWebhookPayload body,
                                       @RequestHeader(value = "Authorization",
                                               required = false) String authHeader) {
        if (body == null || body.getType() == null) {
            return ResponseEntity.badRequest().build();
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
}
