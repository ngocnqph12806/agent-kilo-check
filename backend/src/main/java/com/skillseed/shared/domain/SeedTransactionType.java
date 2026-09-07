package com.skillseed.shared.domain;

public enum SeedTransactionType {
    EARN("earn"),
    SPEND("spend"),
    GRANT("grant"),
    EXPIRE("expire"),
    REFUND("refund");

    private final String dbValue;

    SeedTransactionType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
