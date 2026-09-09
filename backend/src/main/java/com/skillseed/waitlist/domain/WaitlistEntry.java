package com.skillseed.waitlist.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Marketing waitlist signup from the public landing page. The endpoint is
 * unauthenticated and rate-limited per IP; emails are stored lower-cased
 * so duplicate signups dedupe naturally.
 */
@Entity
@Table(name = "waitlist")
public class WaitlistEntry {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 255, updatable = false)
    private String email;

    @Column(name = "email_lower", nullable = false, length = 255, updatable = false)
    private String emailLower;

    @Column(name = "source", nullable = false, length = 40, updatable = false)
    private String source = "landing";

    @Column(name = "referrer", length = 255, updatable = false)
    private String referrer;

    @Column(name = "user_agent", length = 255, updatable = false)
    private String userAgent;

    @Column(name = "ip_address", length = 64, updatable = false)
    private String ipAddress;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected WaitlistEntry() {
        // JPA
    }

    public WaitlistEntry(String email, String source, String referrer,
                         String userAgent, String ipAddress) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.emailLower = email.toLowerCase(java.util.Locale.ROOT);
        this.source = source == null || source.isBlank() ? "landing" : source;
        this.referrer = referrer;
        this.userAgent = truncate(userAgent, 255);
        this.ipAddress = truncate(ipAddress, 64);
        this.createdAt = Instant.now();
    }

    public void markConfirmed() {
        this.confirmedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailLower() {
        return emailLower;
    }

    public String getSource() {
        return source;
    }

    public String getReferrer() {
        return referrer;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
