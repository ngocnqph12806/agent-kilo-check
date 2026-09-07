package com.skillseed.rating.domain;

import com.skillseed.booking.domain.Booking;
import com.skillseed.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ratee_id", nullable = false)
    private User ratee;

    @Column(name = "overall_score")
    private Short overallScore;

    @Column(name = "review_text", columnDefinition = "text")
    private String reviewText;

    @Column(name = "helpfulness_score")
    private Short helpfulnessScore;

    @Column(name = "respectfulness_score")
    private Short respectfulnessScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Rating() {
    }

    public Rating(UUID id, Booking booking, User rater, User ratee) {
        this.id = id;
        this.booking = booking;
        this.rater = rater;
        this.ratee = ratee;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public User getRater() {
        return rater;
    }

    public void setRater(User rater) {
        this.rater = rater;
    }

    public User getRatee() {
        return ratee;
    }

    public void setRatee(User ratee) {
        this.ratee = ratee;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
