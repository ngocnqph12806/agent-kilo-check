package com.skillseed.notification.domain;

import com.skillseed.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * In-app notification. Payload is a free-form {@code JSONB} so we can add
 * new event types without a schema migration; the FE chooses how to render
 * based on {@link #type}.
 *
 * <p>Soft-unread: {@code readAt == null} means the user has not opened
 * the notification yet. Setting {@code readAt} marks it as read.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50, updatable = false)
    private NotificationType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, updatable = false,
            columnDefinition = "jsonb")
    private Map<String, Object> payload = Map.of();

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Notification() {
    }

    public Notification(UUID id, User user, NotificationType type, Map<String, Object> payload) {
        this.id = id;
        this.user = user;
        this.type = type;
        this.payload = payload == null ? Map.of() : payload;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public NotificationType getType() {
        return type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void markRead(Instant when) {
        this.readAt = when;
    }

    public boolean isUnread() {
        return readAt == null;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
