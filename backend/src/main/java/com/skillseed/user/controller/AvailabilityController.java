package com.skillseed.user.controller;

import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.dto.AvailabilitySlotResponse;
import com.skillseed.user.dto.ReplaceAvailabilityRequest;
import com.skillseed.user.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Weekly availability endpoints (T-M44, FR-M12). PUT replaces the full
 * slot set; GET returns the current set ordered by day/time.
 */
@RestController
@RequestMapping("/api/v1/users/me/availability")
@Tag(name = "Users · Availability", description = "Recurring weekly availability slots")
public class AvailabilityController {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List the authenticated user's weekly availability slots")
    public ResponseEntity<List<AvailabilitySlotResponse>> list() {
        return ResponseEntity.ok(service.list(CurrentUser.requireId()));
    }

    @PutMapping
    @Operation(summary = "Replace all availability slots (bulk)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Slots replaced"),
            @ApiResponse(responseCode = "400", description = "Validation failed (range / overlap)")
    })
    public ResponseEntity<List<AvailabilitySlotResponse>> replace(
            @Valid @RequestBody ReplaceAvailabilityRequest req) {
        return ResponseEntity.ok(service.replace(CurrentUser.requireId(), req));
    }
}