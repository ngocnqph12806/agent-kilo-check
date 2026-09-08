package com.skillseed.rating.controller;

import com.skillseed.rating.dto.CreateRatingRequest;
import com.skillseed.rating.dto.RatingPageResponse;
import com.skillseed.rating.dto.RatingResponse;
import com.skillseed.rating.service.RatingService;
import com.skillseed.shared.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/api/v1/ratings")
    public ResponseEntity<RatingResponse> create(@Valid @RequestBody CreateRatingRequest req) {
        UUID actorId = CurrentUser.requireId();
        RatingResponse created = ratingService.create(actorId, req);
        return ResponseEntity.created(URI.create("/api/v1/ratings/" + created.getId()))
                .body(created);
    }

    @GetMapping("/api/v1/users/{id}/ratings")
    public ResponseEntity<RatingPageResponse> listForUser(
            @PathVariable("id") UUID userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(ratingService.listForUser(userId, page, size));
    }
}
