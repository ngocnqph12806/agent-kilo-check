package com.skillseed.user.controller;

import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.dto.CreateWantedSkillRequest;
import com.skillseed.user.dto.UpdateWantedSkillRequest;
import com.skillseed.user.dto.WantedSkillResponse;
import com.skillseed.user.service.WantedSkillService;
import io.swagger.v3.oas.annotations.Operation;
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
 * {@code user_skills_wanted} rows (T-M43, FR-M11).
 */
@RestController
@RequestMapping("/api/v1/users/me/skills/wanted")
@Tag(name = "Users · Skills Wanted", description = "Manage skills the user wants to learn")
public class WantedSkillController {

    private final WantedSkillService service;

    public WantedSkillController(WantedSkillService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List the authenticated user's wanted skills")
    public ResponseEntity<List<WantedSkillResponse>> list() {
        return ResponseEntity.ok(service.list(CurrentUser.requireId()));
    }

    @PostMapping
    @Operation(summary = "Add a skill to the user's wanted list")
    public ResponseEntity<WantedSkillResponse> create(
            @Valid @RequestBody CreateWantedSkillRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(CurrentUser.requireId(), req));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a wanted skill row")
    public ResponseEntity<WantedSkillResponse> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateWantedSkillRequest req) {
        return ResponseEntity.ok(service.update(CurrentUser.requireId(), id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a wanted skill row")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        service.delete(CurrentUser.requireId(), id);
        return ResponseEntity.noContent().build();
    }
}