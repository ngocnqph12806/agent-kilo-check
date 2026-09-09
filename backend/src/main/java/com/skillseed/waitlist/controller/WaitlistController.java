package com.skillseed.waitlist.controller;

import com.skillseed.waitlist.dto.JoinWaitlistRequest;
import com.skillseed.waitlist.dto.WaitlistResponse;
import com.skillseed.waitlist.service.WaitlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public marketing waitlist endpoint (Phase 1, T-M423). Idempotent and
 * rate-limited; no authentication required.
 */
@RestController
@RequestMapping("/api/v1/waitlist")
@Tag(name = "Waitlist", description = "Marketing waitlist signups from the landing page")
public class WaitlistController {

    private final WaitlistService waitlistService;

    public WaitlistController(WaitlistService waitlistService) {
        this.waitlistService = waitlistService;
    }

    @PostMapping
    @Operation(summary = "Join the marketing waitlist (idempotent, rate-limited)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Already on the list or newly added"),
            @ApiResponse(responseCode = "400", description = "Invalid email"),
            @ApiResponse(responseCode = "429", description = "Too many signups from this IP")
    })
    public ResponseEntity<WaitlistResponse> join(
            @Valid @RequestBody JoinWaitlistRequest req,
            HttpServletRequest httpRequest) {
        WaitlistResponse res = waitlistService.join(req, httpRequest);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
