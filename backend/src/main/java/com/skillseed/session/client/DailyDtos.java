package com.skillseed.session.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Minimal Daily.co REST contract for the MVP — we only need to
 * create / fetch a room and mint a meeting token.
 *
 * <p>See <a href="https://docs.daily.co/reference/rest-api">Daily REST API</a>.
 */
public final class DailyDtos {

    private DailyDtos() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateRoomRequest {
        @JsonProperty("name")
        private String name;
        @JsonProperty("privacy")
        private final String privacy = "private";
        @JsonProperty("properties")
        private RoomProperties properties = new RoomProperties();

        public CreateRoomRequest() {
        }

        public CreateRoomRequest(String name, Instant expiresAt) {
            this.name = name;
            this.properties.setExp(expiresAt);
        }

        public String getName() {
            return name;
        }

        public String getPrivacy() {
            return privacy;
        }

        public RoomProperties getProperties() {
            return properties;
        }
    }

    public static class RoomProperties {
        @JsonProperty("exp")
        private Instant exp;
        @JsonProperty("enable_chat")
        private final boolean enableChat = true;
        @JsonProperty("enable_screenshare")
        private final boolean enableScreenshare = true;
        @JsonProperty("enable_knocking")
        private final boolean enableKnocking = false;
        @JsonProperty("start_video_off")
        private final boolean startVideoOff = false;
        @JsonProperty("start_audio_off")
        private final boolean startAudioOff = false;
        @JsonProperty("max_participants")
        private final int maxParticipants = 4;

        public Instant getExp() {
            return exp;
        }

        public void setExp(Instant exp) {
            this.exp = exp;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoomResponse {
        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("url")
        private String url;
        @JsonProperty("privacy")
        private String privacy;
        @JsonProperty("config")
        private RoomProperties config;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public RoomProperties getConfig() {
            return config;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateMeetingTokenRequest {
        @JsonProperty("properties")
        private final MeetingTokenProperties properties = new MeetingTokenProperties();

        public CreateMeetingTokenRequest() {
        }

        public CreateMeetingTokenRequest(String userName, String userId,
                                         boolean isOwner, Instant expiresAt) {
            this.properties.setUserName(userName);
            this.properties.setUserId(userId);
            this.properties.setIsOwner(isOwner);
            this.properties.setRoomName(null);
            this.properties.setExp(expiresAt);
        }

        public MeetingTokenProperties getProperties() {
            return properties;
        }
    }

    public static class MeetingTokenProperties {
        @JsonProperty("room_name")
        private String roomName;
        @JsonProperty("user_name")
        private String userName;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("is_owner")
        private boolean isOwner;
        @JsonProperty("exp")
        private Instant exp;

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public boolean getIsOwner() {
            return isOwner;
        }

        public void setIsOwner(boolean isOwner) {
            this.isOwner = isOwner;
        }

        public Instant getExp() {
            return exp;
        }

        public void setExp(Instant exp) {
            this.exp = exp;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeetingTokenResponse {
        @JsonProperty("token")
        private String token;

        public String getToken() {
            return token;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyError {
        @JsonProperty("error")
        private String error;
        @JsonProperty("info")
        private Object info;

        public String getError() {
            return error;
        }

        public Object getInfo() {
            return info;
        }
    }
}
