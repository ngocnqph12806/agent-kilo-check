package com.skillseed.user.domain;

import jakarta.persistence.Column;
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

/**
 * Linked Web3 wallet for a user. One user may own multiple wallets; each
 * wallet address (case-insensitive) may belong to at most one user —
 * enforced by {@code UNIQUE (address_lower)} on the table.
 *
 * <p>Authentication flows: client signs an EIP-4361 SIWE message; the
 * backend recovers the signer address, looks up the row by
 * {@code address_lower}, and issues the JWT pair on success.
 */
@Entity
@Table(name = "user_wallets")
public class UserWallet {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "address", nullable = false, length = 42, updatable = false)
    private String address;

    /** Lower-cased copy of {@link #address} for case-insensitive lookups. */
    @Column(name = "address_lower", nullable = false, length = 42, updatable = false)
    private String addressLower;

    @Column(name = "chain_id", nullable = false, updatable = false)
    private long chainId;

    @Column(name = "ens_name", length = 255)
    private String ensName;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "linked_at", nullable = false, updatable = false)
    private Instant linkedAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    protected UserWallet() {
        // JPA
    }

    public UserWallet(User user, String address, long chainId) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.address = address;
        this.addressLower = address.toLowerCase(java.util.Locale.ROOT);
        this.chainId = chainId;
        this.primary = false;
        this.linkedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressLower() {
        return addressLower;
    }

    public long getChainId() {
        return chainId;
    }

    public String getEnsName() {
        return ensName;
    }

    public void setEnsName(String ensName) {
        this.ensName = ensName;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Instant getLinkedAt() {
        return linkedAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public void touchLastUsed() {
        this.lastUsedAt = Instant.now();
    }
}
