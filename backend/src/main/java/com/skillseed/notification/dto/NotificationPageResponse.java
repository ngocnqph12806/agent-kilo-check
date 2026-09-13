package com.skillseed.notification.dto;

import com.skillseed.shared.dto.PageResponse;

import java.util.List;

/**
 * Module-specific page envelope for the notification inbox — implements
 * the shared {@link PageResponse} contract (T-M410) and adds an
 * {@code unreadCount} field used by the FE badge / poll.
 */
public record NotificationPageResponse(
        List<NotificationResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        long unreadCount,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) implements PageResponse<NotificationResponse> {

    public NotificationPageResponse(List<NotificationResponse> content, int page, int size,
                                    long totalElements, int totalPages, long unreadCount) {
        this(content, page, size, totalElements, totalPages, unreadCount,
                page == 0,
                page >= Math.max(1, totalPages) - 1,
                page < Math.max(1, totalPages) - 1,
                page > 0);
    }
}
