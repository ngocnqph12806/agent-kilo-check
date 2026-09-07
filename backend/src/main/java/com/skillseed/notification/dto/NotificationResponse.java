package com.skillseed.notification.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        Map<String, Object> payload,
        boolean unread,
        Instant createdAt,
        Instant readAt) {
}
