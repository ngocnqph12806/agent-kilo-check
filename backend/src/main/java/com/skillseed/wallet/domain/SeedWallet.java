package com.skillseed.wallet.domain;

import com.skillseed.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seed_wallets")
public class SeedWallet {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "balance_cached", nullable = false)
    private int balanceCached = 0;

    @Column(name = "total_earned", nullable = false)
    private int totalEarned = 0;

    @Column(name = "total_spent", nullable = false)
    private int totalSpent = 0;

    @Column(name = "last_expiring_at")
    private Instant lastExpiringAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public SeedWallet() {
    }

    public SeedWallet(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getBalanceCached() {
        return balanceCached;
    }

    public void setBalanceCached(int balanceCached) {
        this.balanceCached = balanceCached;
    }

    public int getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(int totalEarned) {
        this.totalEarned = totalEarned;
    }

    public int getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(int totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Instant getLastExpiringAt() {
        return lastExpiringAt;
    }

    public void setLastExpiringAt(Instant lastExpiringAt) {
        this.lastExpiringAt = lastExpiringAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
