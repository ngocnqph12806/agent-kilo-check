package com.skillseed.discover.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DiscoverPageResponse(
        @JsonProperty("content") @JsonAlias("items") List<DiscoverMatchResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) {

    public DiscoverPageResponse(List<DiscoverMatchResponse> items, int page, int size,
                                long totalElements, int totalPages) {
        this(items, page, size, totalElements, totalPages,
                page == 0,
                page >= Math.max(1, totalPages) - 1,
                page < Math.max(1, totalPages) - 1,
                page > 0);
    }
}
