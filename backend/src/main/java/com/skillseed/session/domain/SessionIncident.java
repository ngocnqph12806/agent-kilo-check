package com.skillseed.session.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * FR-M57 incident ticket produced by the in-session "Report issue"
 * button. Status flows open → investigating → resolved (or dismissed).
 */
@Entity
@Table(name = "session_incidents")
public class SessionIncident {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "booking_id", nullable = false, updatable = false)
    private UUID bookingId;

    @Column(name = "reporter_id", nullable = false, updatable = false)
    private UUID reporterId;

    @Column(name = "category", nullable = false, updatable = false, length = 40)
    private String category;

    @Column(name = "description", nullable = false, updatable = false, columnDefinition = "text")
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "open";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    protected SessionIncident() {
    }

    public SessionIncident(UUID id, UUID bookingId, UUID reporterId, String category,
                           String description, Instant createdAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.reporterId = reporterId;
        this.category = category;
        this.description = description;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }
    public UUID getBookingId() {
        return bookingId;
    }
    public UUID getReporterId() {
        return reporterId;
    }
    public String getCategory() {
        return category;
    }
    public String getDescription() {
        return description;
    }
    public String getStatus() {
        return status;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
