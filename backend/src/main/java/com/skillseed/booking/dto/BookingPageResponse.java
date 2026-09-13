package com.skillseed.booking.dto;

import com.skillseed.shared.dto.PageResponse;

import java.util.List;

/**
 * Module-specific page envelope for bookings — implements the shared
 * {@link PageResponse} contract (T-M410) so callers can depend on the
 * interface while this class keeps the original {@code BookingSummaryResponse}
 * parameterisation and convenience constructor.
 */
public record BookingPageResponse(
        List<BookingSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) implements PageResponse<BookingSummaryResponse> {

    public BookingPageResponse(List<BookingSummaryResponse> content, int page, int size,
                               long totalElements, int totalPages) {
        this(content, page, size, totalElements, totalPages,
                page == 0,
                page >= Math.max(1, totalPages) - 1,
                page < Math.max(1, totalPages) - 1,
                page > 0);
    }
}
