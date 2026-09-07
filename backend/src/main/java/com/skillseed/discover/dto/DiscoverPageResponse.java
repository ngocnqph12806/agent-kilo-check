package com.skillseed.discover.dto;

import java.util.List;

public record DiscoverPageResponse(
        List<DiscoverMatchResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
