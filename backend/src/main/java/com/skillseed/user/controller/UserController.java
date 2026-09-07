package com.skillseed.user.controller;

import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.dto.CurrentUserResponse;
import com.skillseed.user.dto.FreeSlotResponse;
import com.skillseed.user.dto.PublicUserResponse;
import com.skillseed.user.dto.UpdateProfileRequest;
import com.skillseed.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * User-profile endpoints (T-M30, T-M31, T-M33). Avatar upload is in
 * {@link AvatarController}; onboarding lives in
 * {@link OnboardingController}.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User profile, Skill DNA, public profile")
public class UserController {

    private static final int MAX_AVAILABILITY_DAYS = 60;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user's profile + Skill DNA summary")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Profile returned"))
    public ResponseEntity<CurrentUserResponse> me() {
        return ResponseEntity.ok(userService.getCurrentUser(CurrentUser.requireId()));
    }

    @PatchMapping("/me")
    @Operation(summary = "Partially update the authenticated user's profile")
    public ResponseEntity<CurrentUserResponse> updateMe(
            @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(userService.updateProfile(CurrentUser.requireId(), req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get another user's public profile (no email / phone)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Public profile returned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<PublicUserResponse> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.getPublicProfile(id));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Materialise free slots for the next N days (T-M81)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Free slots returned (possibly empty)")
    })
    public ResponseEntity<List<FreeSlotResponse>> getAvailability(
            @PathVariable("id") UUID id,
            @Parameter(description = "Window start (ISO-8601 instant). Defaults to now.")
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @Parameter(description = "Window length in days (1..60). Defaults to 7.")
            @RequestParam(name = "days", defaultValue = "7") int days) {
        Instant start = from == null ? Instant.now() : from;
        int safeDays = Math.max(1, Math.min(days, MAX_AVAILABILITY_DAYS));
        return ResponseEntity.ok(userService.getFreeSlots(id, start, safeDays));
    }
}