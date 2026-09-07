package com.skillseed.session.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the Daily.co integration. Bound from
 * the {@code session.daily.*} prefix in application.yml.
 *
 * <p>{@code enabled} defaults to {@code false}; when disabled
 * the {@link DailyClient} returns a deterministic placeholder
 * so local development works without real credentials.
 */
@ConfigurationProperties(prefix = "session.daily")
public class DailyProperties {

    private boolean enabled = false;
    private String apiKey = "";
    private String apiBase = "https://api.daily.co/v1";
    private int graceMinutes = 30;
    private String webhookSigningKey = "";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiBase() {
        return apiBase;
    }

    public void setApiBase(String apiBase) {
        this.apiBase = apiBase;
    }

    public int getGraceMinutes() {
        return graceMinutes;
    }

    public void setGraceMinutes(int graceMinutes) {
        this.graceMinutes = graceMinutes;
    }

    public String getWebhookSigningKey() {
        return webhookSigningKey;
    }

    public void setWebhookSigningKey(String webhookSigningKey) {
        this.webhookSigningKey = webhookSigningKey;
    }
}
