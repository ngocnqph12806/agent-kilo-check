package com.skillseed.shared.dto;

import java.util.List;

/**
 * Shared pagination envelope per AGENTS.md §5.5 and
 * SKILLSEED_API_AND_DB.md §2.3. All paginated endpoints must return
 * this shape; bespoke per-module wrappers are not allowed.
 *
 * <p>JSON contract (matches spec):
 * <pre>
 * {
 *   "content":         [...],
 *   "page":            0,
 *   "size":            20,
 *   "totalElements":   142,
 *   "totalPages":      8,
 *   "first":           true,
 *   "last":            false,
 *   "hasNext":         true,
 *   "hasPrevious":     false
 * }
 * </pre>
 *
 * <p>{@code content} may be an empty array (never {@code null}).
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) {

    public static <T> PageResponse<T> of(List<T> content, int page, int size,
                                          long totalElements, int totalPages) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int safeTotalPages = Math.max(1, totalPages);
        boolean first = safePage == 0;
        boolean last = safePage >= safeTotalPages - 1;
        boolean hasNext = !last;
        boolean hasPrevious = !first;
        List<T> safeContent = content == null ? List.of() : content;
        return new PageResponse<>(safeContent, safePage, safeSize, totalElements,
                safeTotalPages, first, last, hasNext, hasPrevious);
    }
}
