package com.skillseed.booking.dto;

import java.util.List;

public record BookingPageResponse(
        List<BookingSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) {

    public BookingPageResponse(List<BookingSummaryResponse> content, int page, int size,
                               long totalElements, int totalPages) {
        this(content, page, size, totalElements, totalPages,
                page == 0,
                page >= Math.max(1, totalPages) - 1,
                page < Math.max(1, totalPages) - 1,
                page > 0);
    }
}
