package com.skillseed.shared.dto;

import java.util.List;

/**
 * Default {@link PageResponse} implementation — use {@link PageResponse#of}
 * to construct without naming this type explicitly.
 */
public record DefaultPageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) implements PageResponse<T> {

    public DefaultPageResponse(List<T> content, int page, int size,
                               long totalElements, int totalPages) {
        this(content, page, size, totalElements, totalPages,
                page == 0,
                page >= Math.max(1, totalPages) - 1,
                page < Math.max(1, totalPages) - 1,
                page > 0);
    }
}
