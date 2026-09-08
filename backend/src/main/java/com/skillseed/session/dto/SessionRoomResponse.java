package com.skillseed.session.dto;

import java.time.Instant;

public class SessionRoomResponse {

    private String roomUrl;
    private String roomName;
    private String token;
    private String role;
    private Instant expiresAt;
    private Instant scheduledAt;
    private int durationMinutes;

    public SessionRoomResponse() {
    }

    public SessionRoomResponse(String roomUrl, String roomName, String token, String role,
                               Instant expiresAt, Instant scheduledAt, int durationMinutes) {
        this.roomUrl = roomUrl;
        this.roomName = roomName;
        this.token = token;
        this.role = role;
        this.expiresAt = expiresAt;
        this.scheduledAt = scheduledAt;
        this.durationMinutes = durationMinutes;
    }

    public String getRoomUrl() {
        return roomUrl;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
