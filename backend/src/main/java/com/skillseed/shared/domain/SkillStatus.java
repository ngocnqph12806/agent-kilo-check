package com.skillseed.shared.domain;

/**
 * Review lifecycle of a {@code Skill} row.
 *
 * <p>Seeded skills are {@link #APPROVED}; user-created ("custom") skills
 * start as {@link #PENDING_REVIEW} until an admin flips them to
 * {@link #APPROVED} or {@link #REJECTED}.
 */
public enum SkillStatus {
    PENDING_REVIEW("pending_review"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String dbValue;

    SkillStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}