package com.skillseed.wallet.dto;

import java.time.Instant;

public record ExpiringSoonResponse(
        int amount,
        Instant oldestExpiresAt) {
}