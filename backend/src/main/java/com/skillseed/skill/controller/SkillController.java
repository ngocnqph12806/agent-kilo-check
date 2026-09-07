package com.skillseed.skill.controller;

import com.skillseed.shared.domain.SkillCategory;
import com.skillseed.shared.security.CurrentUser;
import com.skillseed.skill.dto.CreateCustomSkillRequest;
import com.skillseed.skill.dto.SkillResponse;
import com.skillseed.skill.exception.SkillException;
import com.skillseed.skill.service.SkillService;
import com.skillseed.skill.service.SkillService.SkillPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.UUID;

/**
 * Skill taxonomy endpoints (T-M40, T-M41). Read endpoints are public
 * (see {@code SecurityConfig.PUBLIC_PATHS}); {@code POST /skills}
 * requires authentication.
 */
@RestController
@RequestMapping("/api/v1/skills")
@Tag(name = "Skills", description = "Skill taxonomy search and custom submissions")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @Operation(summary = "Search skills (name LIKE + category filter, paginated)")
    @ApiResponse(responseCode = "200", description = "Page of approved skills")
    public ResponseEntity<SkillPage> search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        SkillCategory categoryEnum = parseCategoryOrNull(category);
        return ResponseEntity.ok(skillService.search(query, categoryEnum, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single skill by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill returned"),
            @ApiResponse(responseCode = "404", description = "Skill not found")
    })
    public ResponseEntity<SkillResponse> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(skillService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a custom skill (lands in pending_review)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Skill created, pending admin review"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Slug already exists")
    })
    public ResponseEntity<SkillResponse> createCustom(@Valid @RequestBody CreateCustomSkillRequest req) {
        CurrentUser.require();
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createCustomSkill(req));
    }

    private SkillCategory parseCategoryOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        for (SkillCategory c : SkillCategory.values()) {
            if (c.getDbValue().equals(value)) {
                return c;
            }
        }
        throw SkillException.badRequest("SKILL_CATEGORY_INVALID",
                "Unknown category: " + raw);
    }
}