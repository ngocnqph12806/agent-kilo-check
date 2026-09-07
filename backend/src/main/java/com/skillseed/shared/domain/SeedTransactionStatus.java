package com.skillseed.shared.domain;

public enum SeedTransactionStatus {
    PENDING("pending"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    private final String dbValue;

    SeedTransactionStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
