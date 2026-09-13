package com.skillseed.discover.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillseed.shared.dto.PageResponse;

import java.util.List;

/**
 * Module-specific page envelope for discover matches — implements the
 * shared {@link PageResponse} contract (T-M410). The {@code content}
 * field is aliased as {@code items} on the wire so legacy FE clients
 * that read {@code items} keep working; the canonical JSON key is
 * {@code content}.
 */
public record DiscoverPageResponse(
        @JsonProperty("content") @JsonAlias("items") List<DiscoverMatchResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) implements PageResponse<DiscoverMatchResponse> {

    public DiscoverPageResponse(List<DiscoverMatchResponse> content, int page, int size,
                                long totalElements, int totalPages) {
        this(content, page, size, totalElements, totalPages,
                page == 0,
                page >= Math.max(1, totalPages) - 1,
                page < Math.max(1, totalPages) - 1,
                page > 0);
    }
}
