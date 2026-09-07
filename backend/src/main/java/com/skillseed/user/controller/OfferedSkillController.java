package com.skillseed.user.controller;

import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.dto.CreateOfferedSkillRequest;
import com.skillseed.user.dto.OfferedSkillResponse;
import com.skillseed.user.dto.UpdateOfferedSkillRequest;
import com.skillseed.user.service.OfferedSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * CRUD endpoints for the authenticated user's
 * {@code user_skills_offered} rows (T-M42, FR-M10).
 */
@RestController
@RequestMapping("/api/v1/users/me/skills/offered")
@Tag(name = "Users · Skills Offered", description = "Manage skills the user can teach")
public class OfferedSkillController {

    private final OfferedSkillService service;

    public OfferedSkillController(OfferedSkillService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List the authenticated user's offered skills")
    public ResponseEntity<List<OfferedSkillResponse>> list() {
        return ResponseEntity.ok(service.list(CurrentUser.requireId()));
    }

    @PostMapping
    @Operation(summary = "Add a skill to the user's offered list")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Offered skill created"),
            @ApiResponse(responseCode = "409", description = "Already offering this skill")
    })
    public ResponseEntity<OfferedSkillResponse> create(
            @Valid @RequestBody CreateOfferedSkillRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(CurrentUser.requireId(), req));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an offered skill row")
    public ResponseEntity<OfferedSkillResponse> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateOfferedSkillRequest req) {
        return ResponseEntity.ok(service.update(CurrentUser.requireId(), id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove an offered skill row")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        service.delete(CurrentUser.requireId(), id);
        return ResponseEntity.noContent().build();
    }
}