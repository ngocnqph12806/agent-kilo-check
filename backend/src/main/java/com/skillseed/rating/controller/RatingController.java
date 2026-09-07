package com.skillseed.rating.controller;

import com.skillseed.rating.dto.CreateRatingRequest;
import com.skillseed.rating.dto.RatingPageResponse;
import com.skillseed.rating.dto.RatingResponse;
import com.skillseed.rating.service.RatingService;
import com.skillseed.shared.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Rating REST controller (T-M171). Public list endpoint powers the
 * reviews tab on the user profile; the write endpoint is JWT-gated.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Rating", description = "Session ratings and reviews")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/ratings")
    @Operation(summary = "Submit a rating for a completed booking")
    public ResponseEntity<RatingResponse> submit(@Valid @RequestBody CreateRatingRequest req) {
        RatingResponse response = ratingService.submit(CurrentUser.requireId(), req);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/users/{id}/ratings")
    @Operation(summary = "List ratings received by a user (paged)")
    public RatingPageResponse listForUser(
            @PathVariable("id") UUID rateeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ratingService.listForUser(rateeId, page, size);
    }
}
