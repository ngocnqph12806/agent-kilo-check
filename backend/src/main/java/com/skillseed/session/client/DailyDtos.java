package com.skillseed.session.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Wire-format DTOs that mirror the Daily.co REST API
 * ({@code https://api.daily.co/v1/rooms} and {@code /meeting-tokens}).
 *
 * <p>Only the fields we actually consume are mapped. Jackson
 * is configured to ignore unknown properties so the API may
 * add more without breaking us.
 */
public final class DailyDtos {

    private DailyDtos() {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateRoomRequest {
        @JsonProperty("name")
        private String name;
        @JsonProperty("privacy")
        private String privacy = "private";
        @JsonProperty("properties")
        private RoomProperties properties;

        public CreateRoomRequest(String name, RoomProperties properties) {
            this.name = name;
            this.properties = properties;
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
        private long exp;
        @JsonProperty("enable_chat")
        private boolean enableChat = true;
        @JsonProperty("enable_screenshare")
        private boolean enableScreenshare = true;
        @JsonProperty("start_video_off")
        private boolean startVideoOff;
        @JsonProperty("start_audio_off")
        private boolean startAudioOff;

        public RoomProperties(long exp, boolean enableChat, boolean enableScreenshare) {
            this.exp = exp;
            this.enableChat = enableChat;
            this.enableScreenshare = enableScreenshare;
        }

        public long getExp() {
            return exp;
        }

        public boolean isEnableChat() {
            return enableChat;
        }

        public boolean isEnableScreenshare() {
            return enableScreenshare;
        }

        public boolean isStartVideoOff() {
            return startVideoOff;
        }

        public boolean isStartAudioOff() {
            return startAudioOff;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateRoomResponse {
        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("url")
        private String url;
        @JsonProperty("privacy")
        private String privacy;
        @JsonProperty("config")
        private RoomConfig config;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getPrivacy() {
            return privacy;
        }

        public RoomConfig getConfig() {
            return config;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setPrivacy(String privacy) {
            this.privacy = privacy;
        }

        public void setConfig(RoomConfig config) {
            this.config = config;
        }
    }

    public static class RoomConfig {
        @JsonProperty("exp")
        private Instant exp;

        public Instant getExp() {
            return exp;
        }

        public void setExp(Instant exp) {
            this.exp = exp;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateMeetingTokenRequest {
        @JsonProperty("properties")
        private MeetingTokenProperties properties;

        public CreateMeetingTokenRequest(MeetingTokenProperties properties) {
            this.properties = properties;
        }

        public MeetingTokenProperties getProperties() {
            return properties;
        }
    }

    public static class MeetingTokenProperties {
        @JsonProperty("room_name")
        private String roomName;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("user_name")
        private String userName;
        @JsonProperty("is_owner")
        private boolean owner;
        @JsonProperty("exp")
        private long exp;

        public MeetingTokenProperties(String roomName, String userId, String userName,
                                      boolean owner, long exp) {
            this.roomName = roomName;
            this.userId = userId;
            this.userName = userName;
            this.owner = owner;
            this.exp = exp;
        }

        public String getRoomName() {
            return roomName;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public boolean isOwner() {
            return owner;
        }

        public long getExp() {
            return exp;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateMeetingTokenResponse {
        @JsonProperty("token")
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    /**
     * Server-side error envelope returned by Daily.
     */
    public static class ApiError {
        @JsonProperty("error")
        private String error;
        @JsonProperty("info")
        private ApiErrorInfo info;

        public String getError() {
            return error;
        }

        public ApiErrorInfo getInfo() {
            return info;
        }
    }

    public static class ApiErrorInfo {
        @JsonProperty("code")
        private String code;
        @JsonProperty("message")
        private String message;
        @JsonProperty("errors")
        private List<String> errors;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}
