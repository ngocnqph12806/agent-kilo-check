package com.skillseed.rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Body of {@code POST /api/v1/ratings}. The validator can submit only the
 * {@code overallScore + reviewText} pair (learner→teacher) while the teacher
 * submits the {@code helpfulnessScore + respectfulnessScore} pair. The
 * rating type is derived server-side from the caller's role on the booking.
 */
public record CreateRatingRequest(
        @NotNull UUID bookingId,
        @Min(1) @Max(5) Short overallScore,
        @Min(1) @Max(5) Short helpfulnessScore,
        @Min(1) @Max(5) Short respectfulnessScore,
        @Size(max = 500) String reviewText) {
}
