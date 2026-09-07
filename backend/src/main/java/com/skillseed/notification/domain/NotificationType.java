package com.skillseed.notification.domain;

/**
 * Stable set of in-app notification types. Mapped 1:1 to the
 * {@code chk_notifications_type} CHECK constraint in
 * {@code V4__add_notifications.sql}.
 */
public enum NotificationType {

    /** Generic welcome message (one-off after registration). */
    WELCOME("welcome"),

    /** Confirmation that the user's email address has been verified. */
    EMAIL_VERIFIED("email_verified"),

    /** A learner requested a booking with the user as teacher. */
    BOOKING_REQUEST("booking_request"),

    /** The teacher accepted the learner's booking. */
    BOOKING_ACCEPTED("booking_accepted"),

    /** The teacher declined the learner's booking. */
    BOOKING_DECLINED("booking_declined"),

    /** Either party cancelled a previously accepted booking. */
    BOOKING_CANCELLED("booking_cancelled"),

    /** Reminder 24h before a confirmed session. */
    BOOKING_REMINDER_24H("booking_reminder_24h"),

    /** Reminder 1h before a confirmed session. */
    BOOKING_REMINDER_1H("booking_reminder_1h"),

    /** The video room for a session has been opened. */
    SESSION_STARTED("session_started"),

    /** The session has been marked complete by one of the participants. */
    SESSION_COMPLETED("session_completed"),

    /** Prompt asking the user to rate a completed session. */
    RATING_PROMPT("rating_prompt"),

    /** Catch-all for operator-driven announcements. */
    SYSTEM("system");

    private final String dbValue;

    NotificationType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
