package com.skillseed.shared.domain;

/**
 * High-level role for the user, used to gate {@code /api/v1/admin/**}
 * endpoints. Kept deliberately small — coarse-grained RBAC is enough for
 * Phase 1 (admin reviews custom skills). Per-resource ownership checks
 * (learner vs teacher on a booking, etc.) are unrelated.
 */
public enum Role {
    USER("user"),
    ADMIN("admin");

    private final String dbValue;

    Role(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
