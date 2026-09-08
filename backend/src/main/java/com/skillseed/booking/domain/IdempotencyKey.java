package com.skillseed.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Idempotency record per FR-M47 / T-M101. Once a (user, endpoint, key)
 * tuple has been seen, subsequent calls replay the cached response
 * instead of executing the underlying action twice (e.g. double-escrow
 * on retried POST /bookings).
 */
@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    @Column(name = "key", nullable = false, updatable = false, length = 255)
    private String key;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "endpoint", nullable = false, updatable = false, length = 64)
    private String endpoint;

    @Column(name = "request_hash", nullable = false, updatable = false, length = 128)
    private String requestHash;

    @Column(name = "response_status", nullable = false, updatable = false)
    private int responseStatus;

    @Column(name = "response_body", nullable = false, updatable = false, columnDefinition = "jsonb")
    private String responseBody;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected IdempotencyKey() {
    }

    public IdempotencyKey(String key, UUID userId, String endpoint, String requestHash,
                          int responseStatus, String responseBody, Instant createdAt) {
        this.key = key;
        this.userId = userId;
        this.endpoint = endpoint;
        this.requestHash = requestHash;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }
    public UUID getUserId() {
        return userId;
    }
    public String getEndpoint() {
        return endpoint;
    }
    public String getRequestHash() {
        return requestHash;
    }
    public int getResponseStatus() {
        return responseStatus;
    }
    public String getResponseBody() {
        return responseBody;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
}
