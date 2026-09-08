package com.skillseed.shared.domain;

public enum BookingStatus {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    DECLINED("declined"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    EXPIRED("expired"),
    NO_SHOW("no_show"),
    RATED("rated");

    private final String dbValue;

    BookingStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    /**
     * Resolves a lowercase dbValue (as used by Spring MVC enum binding
     * and serialized in JSON responses per STATE_MACHINES.md §1.2) to
     * its enum constant. Accepts the legacy Java-name form too
     * (PENDING / CONFIRMED / …) for callers that still send it.
     */
    public static BookingStatus fromDbValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        for (BookingStatus s : values()) {
            if (s.dbValue.equalsIgnoreCase(trimmed) || s.name().equalsIgnoreCase(trimmed)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown booking status: " + value);
    }
}
