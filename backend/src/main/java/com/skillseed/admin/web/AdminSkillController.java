package com.skillseed.admin.web;

import com.skillseed.skill.dto.SkillResponse;
import com.skillseed.skill.service.SkillService;
import com.skillseed.skill.service.SkillService.SkillPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Admin surface for the skill taxonomy (FR-M22). Lets admins review and
 * approve / reject user-submitted custom skills.
 *
 * <p>All endpoints require {@code ROLE_ADMIN}, enforced by
 * {@code @EnableMethodSecurity} + {@link PreAuthorize}.
 */
@RestController
@RequestMapping("/api/v1/admin/skills")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin · Skills", description = "Approve or reject custom skill submissions")
public class AdminSkillController {

    private final SkillService skillService;

    public AdminSkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/pending")
    @Operation(summary = "List custom skills awaiting admin review")
    public ResponseEntity<SkillPage> listPending(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(skillService.listPending(page, size));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a custom skill (status pending_review → approved)")
    public ResponseEntity<SkillResponse> approve(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(skillService.approveSkill(id));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a custom skill (status pending_review → rejected)")
    public ResponseEntity<SkillResponse> reject(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(skillService.rejectSkill(id));
    }
}
