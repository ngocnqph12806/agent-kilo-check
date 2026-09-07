package com.skillseed.wallet.domain;

import com.skillseed.booking.domain.Booking;
import com.skillseed.shared.domain.SeedTransactionStatus;
import com.skillseed.shared.domain.SeedTransactionStatusConverter;
import com.skillseed.shared.domain.SeedTransactionType;
import com.skillseed.shared.domain.SeedTransactionTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seed_transactions")
public class SeedTransaction {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private SeedWallet wallet;

    @Convert(converter = SeedTransactionTypeConverter.class)
    @Column(name = "type", nullable = false, length = 20)
    private SeedTransactionType type;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Convert(converter = SeedTransactionStatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private SeedTransactionStatus status = SeedTransactionStatus.COMPLETED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected SeedTransaction() {
    }

    public SeedTransaction(UUID id, SeedWallet wallet, SeedTransactionType type, int amount, int balanceAfter) {
        this.id = id;
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SeedWallet getWallet() {
        return wallet;
    }

    public void setWallet(SeedWallet wallet) {
        this.wallet = wallet;
    }

    public SeedTransactionType getType() {
        return type;
    }

    public void setType(SeedTransactionType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(int balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public SeedTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(SeedTransactionStatus status) {
        this.status = status;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
