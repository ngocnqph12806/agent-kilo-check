package com.skillseed.discover.controller;

import com.skillseed.discover.dto.DiscoverPageResponse;
import com.skillseed.discover.dto.DiscoverSort;
import com.skillseed.discover.service.DiscoverService;
import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.domain.User;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/discover")
@Validated
@Tag(name = "Discover", description = "Filter-based teacher matching")
public class DiscoverController {

    private final DiscoverService discoverService;
    private final UserRepository userRepository;

    public DiscoverController(DiscoverService discoverService, UserRepository userRepository) {
        this.discoverService = discoverService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "List teacher matches for the current user's wanted skills")
    public ResponseEntity<DiscoverPageResponse> discover(
            @Parameter(description = "Optional skill filter (must be in caller's wanted list)")
            @RequestParam(name = "skill", required = false) UUID skill,
            @Parameter(description = "Filter by common language code")
            @RequestParam(name = "language", required = false) String language,
            @Parameter(description = "Filter by 2-letter ISO country code")
            @RequestParam(name = "country", required = false) String country,
            @Parameter(description = "Minimum rating (inclusive)")
            @RequestParam(name = "minRating", required = false) BigDecimal minRating,
            @Parameter(description = "UTC offset in hours (-12..14) — only teachers whose timezone resolves to that offset are returned")
            @RequestParam(name = "timezoneOffset", required = false) @Min(-12) @Max(14) Integer timezoneOffset,
            @Parameter(description = "Sort: rating | sessions_completed | recent_activity")
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(50) int size) {
        UUID userId = CurrentUser.requireId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound("USER_NOT_FOUND",
                        "Authenticated user no longer exists"));
        return ResponseEntity.ok(discoverService.discover(user, skill, language, country,
                minRating, timezoneOffset, DiscoverSort.fromValue(sort), page, size));
    }
}
