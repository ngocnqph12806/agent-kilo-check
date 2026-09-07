package com.skillseed.rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateRatingRequest {

    @NotNull
    private UUID bookingId;

    @NotNull
    @Min(1)
    @Max(5)
    private Short overallScore;

    @Size(max = 2000)
    private String reviewText;

    @Min(1)
    @Max(5)
    private Short helpfulnessScore;

    @Min(1)
    @Max(5)
    private Short respectfulnessScore;

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public Short getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Short overallScore) {
        this.overallScore = overallScore;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Short getHelpfulnessScore() {
        return helpfulnessScore;
    }

    public void setHelpfulnessScore(Short helpfulnessScore) {
        this.helpfulnessScore = helpfulnessScore;
    }

    public Short getRespectfulnessScore() {
        return respectfulnessScore;
    }

    public void setRespectfulnessScore(Short respectfulnessScore) {
        this.respectfulnessScore = respectfulnessScore;
    }
}
