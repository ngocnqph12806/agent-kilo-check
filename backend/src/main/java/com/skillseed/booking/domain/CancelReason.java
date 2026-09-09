package com.skillseed.booking.domain;

/**
 * Booking cancellation reasons. The enum is wire-stable (lowercase
 * dbValue mirrors the JSON spec) and validated at the DTO layer so the
 * FE can switch on the rendered badge.
 *
 * <p>Labels per screens-svg/04-booking/06-cancel.svg:48-58 (T-M401):
 * "Schedule conflict / Found another mentor / No longer need this skill /
 * Other".
 */
public enum CancelReason {
    // Original four (kept for backwards-compat with existing rows)
    TEACHER_UNAVAILABLE("teacher_unavailable"),
    LEARNER_UNAVAILABLE("learner_unavailable"),
    TECHNICAL_ISSUE("technical_issue"),
    // New three (T-M401) — match the SVG mockup verbatim
    SCHEDULE_CONFLICT("schedule_conflict"),
    FOUND_ANOTHER_MENTOR("found_another_mentor"),
    NO_LONGER_NEEDED("no_longer_needed"),
    OTHER("other");

    private final String dbValue;

    CancelReason(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    /**
     * Resolves a wire value to an enum constant. Accepts both the new
     * canonical dbValue and the legacy enum names for backwards-compat.
     * Unknown values fall back to {@link #OTHER}.
     */
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

    /**
     * Maps legacy enum names to the new SVG-aligned values where possible.
     * Used during the V18 data migration so old bookings carry a sensible
     * reason after upgrade.
     */
    public static CancelReason migrateLegacy(String legacy) {
        if (legacy == null) {
            return OTHER;
        }
        return switch (legacy.toUpperCase(java.util.Locale.ROOT)) {
            case "TEACHER_UNAVAILABLE" -> SCHEDULE_CONFLICT;
            case "LEARNER_UNAVAILABLE" -> NO_LONGER_NEEDED;
            case "TECHNICAL_ISSUE" -> OTHER;
            default -> OTHER;
        };
    }
}
