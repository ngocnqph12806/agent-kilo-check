package com.skillseed.shared.domain;

public enum AuthProvider {
    EMAIL("email"),
    GOOGLE("google"),
    APPLE("apple");

    private final String dbValue;

    AuthProvider(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
