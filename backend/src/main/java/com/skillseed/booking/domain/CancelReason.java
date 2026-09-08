package com.skillseed.booking.domain;

/**
 * Booking cancellation reasons per FR-M45. The enum is wire-stable
 * (lowercase dbValue mirrors the JSON spec) and validated at the DTO
 * layer so the FE can switch on the rendered badge.
 */
public enum CancelReason {
    TEACHER_UNAVAILABLE("teacher_unavailable"),
    LEARNER_UNAVAILABLE("learner_unavailable"),
    TECHNICAL_ISSUE("technical_issue"),
    OTHER("other");

    private final String dbValue;

    CancelReason(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static CancelReason fromValue(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }
        String trimmed = value.trim();
        for (CancelReason r : values()) {
            if (r.dbValue.equalsIgnoreCase(trimmed) || r.name().equalsIgnoreCase(trimmed)) {
                return r;
            }
        }
        return OTHER;
    }
}
