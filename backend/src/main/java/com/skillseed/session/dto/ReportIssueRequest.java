package com.skillseed.session.dto;

import jakarta.validation.constraints.Size;

/**
 * Optional body for {@code POST /api/v1/sessions/{bookingId}/report-issue}.
 * Used by both teacher and learner to flag a problem during a session.
 */
public record ReportIssueRequest(
        @Size(max = 50) String category,
        @Size(max = 1000) String description) {
}
