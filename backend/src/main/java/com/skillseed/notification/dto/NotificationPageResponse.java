package com.skillseed.notification.dto;

import java.util.List;

public record NotificationPageResponse(
        List<NotificationResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        long unreadCount,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) {

    public NotificationPageResponse(List<NotificationResponse> items, int page, int size,
                                    long totalElements, int totalPages, long unreadCount) {
        this(items, page, size, totalElements, totalPages, unreadCount,
                page == 0,
                page >= Math.max(1, totalPages) - 1,
                page < Math.max(1, totalPages) - 1,
                page > 0);
    }
}
