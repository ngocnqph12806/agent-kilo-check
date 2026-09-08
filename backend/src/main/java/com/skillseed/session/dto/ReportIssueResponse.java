package com.skillseed.session.dto;

import java.time.Instant;
import java.util.UUID;

public record ReportIssueResponse(
        UUID incidentId,
        UUID bookingId,
        String status,
        Instant createdAt) {
}
