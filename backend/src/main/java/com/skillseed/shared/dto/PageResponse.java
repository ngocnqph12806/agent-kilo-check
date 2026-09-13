package com.skillseed.shared.dto;

import java.util.List;

/**
 * Shared pagination envelope per AGENTS.md §5.5 and
 * SKILLSEED_API_AND_DB.md §2.3. All paginated endpoints must return
 * a value implementing this interface; bespoke per-module wrappers
 * are not allowed.
 *
 * <p>After T-M410, this is an interface so module-specific envelopes
 * (e.g. {@link com.skillseed.notification.dto.NotificationPageResponse}
 * which adds {@code unreadCount}) can implement it directly while
 * preserving their existing constructors and JSON shape. Use
 * {@link #of(List, int, int, long, int)} to materialise the default
 * implementation {@link DefaultPageResponse}.
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
public interface PageResponse<T> {

    List<T> content();

    int page();

    int size();

    long totalElements();

    int totalPages();

    boolean first();

    boolean last();

    boolean hasNext();

    boolean hasPrevious();

    /**
     * Materialise the default page envelope with computed
     * {@code first/last/hasNext/hasPrevious} flags.
     */
    static <T> PageResponse<T> of(List<T> content, int page, int size,
                                  long totalElements, int totalPages) {
        return new DefaultPageResponse<>(content, page, size, totalElements, totalPages);
    }
}
