package com.skillseed.rating.dto;

import java.util.List;

/**
 * Paged response for {@code GET /api/v1/users/{id}/ratings} and any future
 * paginated rating lookup.
 */
public record RatingPageResponse(
        List<RatingResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        Double averageOverallScore) {
}
