package com.skillseed.session.controller;

import com.skillseed.session.dto.ReportIssueRequest;
import com.skillseed.session.dto.SessionRoomResponse;
import com.skillseed.session.service.SessionService;
import com.skillseed.shared.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Session REST controller (T-M151). Exposes the room + token creation
 * endpoint used by the booking detail page when the user presses
 * <em>Join session</em>.
 */
@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "Session", description = "Video session orchestration")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/{bookingId}/room")
    @Operation(summary = "Create or fetch the Daily.co room for a booking")
    public SessionRoomResponse createRoom(@PathVariable UUID bookingId) {
        return sessionService.createRoom(bookingId, CurrentUser.requireId());
    }

    @PostMapping("/{bookingId}/report-issue")
    @Operation(summary = "Report an issue during an active session")
    public ResponseEntity<Map<String, Object>> reportIssue(
            @PathVariable UUID bookingId,
            @Valid @RequestBody(required = false) ReportIssueRequest req) {
        CurrentUser.requireId();
        return ResponseEntity.accepted().body(Map.of(
                "bookingId", bookingId.toString(),
                "status", "received"
        ));
    }
}
