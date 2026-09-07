package com.skillseed.booking.dto;

import java.util.List;

public record BookingPageResponse(
        List<BookingSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}