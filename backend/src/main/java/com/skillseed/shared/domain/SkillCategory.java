package com.skillseed.shared.domain;

import java.util.Locale;

/**
 * Skill taxonomy. The first eight values match the seeded categories in
 * V2__seed_skills.sql. The last two ({@link #COOKING} and
 * {@link #ACADEMICS}) were added in Sprint 5 (T-M402) to match the
 * filters shown in screens-svg/03-discover/01-discover.svg:56-60 —
 * they're accepted on the wire but stored as their canonical alias
 * (COOKING → LIFE, ACADEMICS → BUSINESS) so existing skill rows stay
 * consistent.
 */
public enum SkillCategory {
    TECH("tech"),
    BUSINESS("business"),
    ART("art"),
    LANGUAGE("language"),
    LIFE("life"),
    HEALTH("health"),
    MUSIC("music"),
    SPORT("sport"),
    // New in Sprint 5 (T-M402). Mapped to existing canonical values via
    // canonical() so the skill row stays in the original taxonomy.
    COOKING("life"),
    ACADEMICS("business");

    private final String dbValue;

    SkillCategory(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    /** Canonical DB value (COOKING → "life", ACADEMICS → "business"). */
    public String canonical() {
        return switch (this) {
            case COOKING -> LIFE.dbValue;
            case ACADEMICS -> BUSINESS.dbValue;
            default -> dbValue;
        };
    }

    /**
     * Resolves a wire value (case-insensitive) to an enum constant,
     * including the two Sprint-5 aliases. Unknown values fall back to
     * {@code null} so callers can decide whether to error or ignore.
     */
    public static SkillCategory fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        for (SkillCategory c : values()) {
            if (c.dbValue.equalsIgnoreCase(trimmed) || c.name().equalsIgnoreCase(trimmed)) {
                return c;
            }
        }
        return null;
    }
}
