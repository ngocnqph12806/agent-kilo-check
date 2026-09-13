package com.skillseed.wallet.dto;

import com.skillseed.shared.dto.PageResponse;

import java.util.List;

/**
 * Module-specific page envelope for seed-transaction history — implements
 * the shared {@link PageResponse} contract (T-M410).
 */
public record SeedTransactionPageResponse(
        List<SeedTransactionResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) implements PageResponse<SeedTransactionResponse> {

    public SeedTransactionPageResponse(List<SeedTransactionResponse> content,
                                       int page, int size, long totalElements, int totalPages) {
        this(content, page, size, totalElements, totalPages,
                page == 0,
                page >= Math.max(1, totalPages) - 1,
                page < Math.max(1, totalPages) - 1,
                page > 0);
    }
}
