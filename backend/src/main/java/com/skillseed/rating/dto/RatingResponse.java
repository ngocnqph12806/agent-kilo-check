package com.skillseed.rating.dto;

import com.skillseed.rating.domain.Rating;

import java.time.Instant;
import java.util.UUID;

/**
 * Response shape for rating endpoints. {@code direction} tells the FE
 * which form was submitted so it can render the right field set.
 */
public record RatingResponse(
        UUID id,
        UUID bookingId,
        UUID raterId,
        String raterName,
        UUID rateeId,
        String rateeName,
        String direction,
        Short overallScore,
        Short helpfulnessScore,
        Short respectfulnessScore,
        String reviewText,
        Instant createdAt) {

    public static RatingResponse from(Rating rating) {
        String direction = (rating.getOverallScore() != null
                && rating.getHelpfulnessScore() == null
                && rating.getRespectfulnessScore() == null)
                ? "learner_to_teacher"
                : "teacher_to_learner";
        return new RatingResponse(
                rating.getId(),
                rating.getBooking().getId(),
                rating.getRater().getId(),
                rating.getRater().getFullName(),
                rating.getRatee().getId(),
                rating.getRatee().getFullName(),
                direction,
                rating.getOverallScore(),
                rating.getHelpfulnessScore(),
                rating.getRespectfulnessScore(),
                rating.getReviewText(),
                rating.getCreatedAt());
    }
}
