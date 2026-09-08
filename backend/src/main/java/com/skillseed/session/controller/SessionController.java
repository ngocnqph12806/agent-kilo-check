package com.skillseed.session.controller;

import com.skillseed.session.dto.ReportIssueRequest;
import com.skillseed.session.dto.ReportIssueResponse;
import com.skillseed.session.dto.SessionRoomResponse;
import com.skillseed.session.service.SessionService;
import com.skillseed.shared.security.CurrentUser;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/{bookingId}/room")
    public ResponseEntity<SessionRoomResponse> openRoom(@PathVariable UUID bookingId) {
        UUID actorId = CurrentUser.requireId();
        SessionRoomResponse room = sessionService.createRoom(bookingId, actorId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/{bookingId}/report-issue")
    @io.swagger.v3.oas.annotations.Operation(summary = "FR-M57: create an incident ticket for an in-session issue")
    public ResponseEntity<ReportIssueResponse> reportIssue(@PathVariable UUID bookingId,
                                                          @Valid @RequestBody ReportIssueRequest body) {
        UUID actorId = CurrentUser.requireId();
        ReportIssueResponse incident = sessionService.reportIssue(bookingId, actorId, body);
        log.info("Session incident opened: booking={} reporter={} category={} incident={}",
                bookingId, actorId, body.getCategory(), incident.incidentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(incident);
    }
}
