package com.skillseed.wallet.dto;

import com.skillseed.wallet.domain.SeedTransaction;

import java.time.Instant;
import java.util.UUID;

public record SeedTransactionResponse(
        UUID id,
        String type,
        int amount,
        int balanceAfter,
        String status,
        UUID bookingId,
        String description,
        Instant expiresAt,
        Instant createdAt) {

    public static SeedTransactionResponse from(SeedTransaction t) {
        return new SeedTransactionResponse(
                t.getId(),
                t.getType().getDbValue(),
                t.getAmount(),
                t.getBalanceAfter(),
                t.getStatus().getDbValue(),
                t.getBooking() == null ? null : t.getBooking().getId(),
                t.getDescription(),
                t.getExpiresAt(),
                t.getCreatedAt());
    }
}