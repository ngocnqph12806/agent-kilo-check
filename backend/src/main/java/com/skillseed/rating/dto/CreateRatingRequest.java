package com.skillseed.rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Rating submission DTO matching SKILLSEED_API_AND_DB.md §9.3.
 *
 * <p>Accepts the 5 sub-scores ({@code knowledge / clarity / helpfulness /
 * punctuality / friendliness}) plus an optional overall recommendation
 * flag. {@code bookingId} is mutually exclusive with the path variable
 * used by {@code POST /sessions/{id}/rate} — the path wins if both are
 * supplied.
 */
public class CreateRatingRequest {

    @NotNull
    private UUID bookingId;

    @NotNull
    @Min(1)
    @Max(5)
    private Short overallScore;

    @Min(1)
    @Max(5)
    private Short knowledgeScore;

    @Min(1)
    @Max(5)
    private Short clarityScore;

    @Min(1)
    @Max(5)
    private Short helpfulnessScore;

    @Min(1)
    @Max(5)
    private Short punctualityScore;

    @Min(1)
    @Max(5)
    private Short friendlinessScore;

    @Min(1)
    @Max(5)
    private Short respectfulnessScore;

    private Boolean wouldRecommend;

    @Size(max = 2000)
    private String reviewText;

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

    public Short getKnowledgeScore() {
        return knowledgeScore;
    }

    public void setKnowledgeScore(Short knowledgeScore) {
        this.knowledgeScore = knowledgeScore;
    }

    public Short getClarityScore() {
        return clarityScore;
    }

    public void setClarityScore(Short clarityScore) {
        this.clarityScore = clarityScore;
    }

    public Short getHelpfulnessScore() {
        return helpfulnessScore;
    }

    public void setHelpfulnessScore(Short helpfulnessScore) {
        this.helpfulnessScore = helpfulnessScore;
    }

    public Short getPunctualityScore() {
        return punctualityScore;
    }

    public void setPunctualityScore(Short punctualityScore) {
        this.punctualityScore = punctualityScore;
    }

    public Short getFriendlinessScore() {
        return friendlinessScore;
    }

    public void setFriendlinessScore(Short friendlinessScore) {
        this.friendlinessScore = friendlinessScore;
    }

    public Short getRespectfulnessScore() {
        return respectfulnessScore;
    }

    public void setRespectfulnessScore(Short respectfulnessScore) {
        this.respectfulnessScore = respectfulnessScore;
    }

    public Boolean getWouldRecommend() {
        return wouldRecommend;
    }

    public void setWouldRecommend(Boolean wouldRecommend) {
        this.wouldRecommend = wouldRecommend;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
