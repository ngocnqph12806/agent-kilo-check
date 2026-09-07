package com.skillseed.rating.dto;

import com.skillseed.rating.domain.Rating;

import java.time.Instant;
import java.util.UUID;

public class RatingResponse {

    private UUID id;
    private UUID bookingId;
    private UUID raterId;
    private UUID rateeId;
    private String raterName;
    private Short overallScore;
    private String reviewText;
    private Short helpfulnessScore;
    private Short respectfulnessScore;
    private boolean autoRated;
    private Instant createdAt;

    public RatingResponse() {
    }

    public static RatingResponse from(Rating rating, String raterName) {
        RatingResponse dto = new RatingResponse();
        dto.id = rating.getId();
        dto.bookingId = rating.getBooking() != null ? rating.getBooking().getId() : null;
        dto.raterId = rating.getRater() != null ? rating.getRater().getId() : null;
        dto.rateeId = rating.getRatee() != null ? rating.getRatee().getId() : null;
        dto.raterName = raterName;
        dto.overallScore = rating.getOverallScore();
        dto.reviewText = rating.getReviewText();
        dto.helpfulnessScore = rating.getHelpfulnessScore();
        dto.respectfulnessScore = rating.getRespectfulnessScore();
        dto.autoRated = rating.isAutoRated();
        dto.createdAt = rating.getCreatedAt();
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getRaterId() {
        return raterId;
    }

    public UUID getRateeId() {
        return rateeId;
    }

    public String getRaterName() {
        return raterName;
    }

    public Short getOverallScore() {
        return overallScore;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Short getHelpfulnessScore() {
        return helpfulnessScore;
    }

    public Short getRespectfulnessScore() {
        return respectfulnessScore;
    }

    public boolean isAutoRated() {
        return autoRated;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
