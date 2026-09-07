package com.skillseed.session.webhook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Subset of the Daily.co webhook payload we consume (see
 * https://docs.daily.co/reference/rest-api/webhooks). Anything we
 * do not read is intentionally absent so the JSON parser tolerates
 * Daily adding new fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyWebhookPayload {

    @JsonProperty("version")
    private String version;

    @JsonProperty("type")
    private String type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("payload")
    private Payload payload;

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Payload getPayload() {
        return payload;
    }

    public static class Payload {
        @JsonProperty("room")
        private Room room;

        public Room getRoom() {
            return room;
        }
    }

    public static class Room {
        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("participants")
        private List<Participant> participants;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<Participant> getParticipants() {
            return participants;
        }
    }

    public static class Participant {
        @JsonProperty("user_id")
        private String userId;

        public String getUserId() {
            return userId;
        }
    }
}
