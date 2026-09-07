package com.skillseed.session.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Daily.co connection + auth settings.
 *
 * <p>Wire {@code session.daily.api-key} via {@code DAILY_API_KEY} env var.
 * All other fields are optional and have sensible defaults for the MVP.
 */
@ConfigurationProperties(prefix = "session.daily")
public class DailyProperties {

    private final String apiKey = "";
    private final String apiBase = "https://api.daily.co/v1";
    private final long defaultRoomTtlMinutes = 60;
    private final long graceMinutes = 30;
    private final String webhookSigningKey = "";

    public String getApiKey() {
        return apiKey;
    }

    public String getApiBase() {
        return apiBase;
    }

    public long getDefaultRoomTtlMinutes() {
        return defaultRoomTtlMinutes;
    }

    public long getGraceMinutes() {
        return graceMinutes;
    }

    public String getWebhookSigningKey() {
        return webhookSigningKey;
    }
}
