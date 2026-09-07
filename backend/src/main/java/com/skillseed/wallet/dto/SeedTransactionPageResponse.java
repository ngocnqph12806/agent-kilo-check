package com.skillseed.wallet.dto;

import java.util.List;

public record SeedTransactionPageResponse(
        List<SeedTransactionResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}