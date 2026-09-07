package com.skillseed.session.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Subset of the Daily.co webhook payload we care about for the MVP. See
 * <a href="https://docs.daily.co/reference/webhooks">Daily webhooks</a>.
 *
 * <p>Other event types are parsed loosely so future additions (e.g.
 * {@code recording.ready-to-download}) don't break the controller.
 */
public final class DailyWebhookPayload {

    private DailyWebhookPayload() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Envelope {
        @JsonProperty("type")
        private String type;
        @JsonProperty("event")
        private Event event;
        @JsonProperty("payload")
        private Map<String, Object> payload;

        public String getType() {
            return type;
        }

        public Event getEvent() {
            return event;
        }

        public Map<String, Object> getPayload() {
            return payload;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Event {
        @JsonProperty("type")
        private String type;
        @JsonProperty("room")
        private String room;
        @JsonProperty("room_name")
        private String roomName;
        @JsonProperty("session_id")
        private String sessionId;

        public String getType() {
            return type;
        }

        public String getRoom() {
            return room;
        }

        public String getRoomName() {
            return roomName;
        }

        public String getSessionId() {
            return sessionId;
        }
    }
}
