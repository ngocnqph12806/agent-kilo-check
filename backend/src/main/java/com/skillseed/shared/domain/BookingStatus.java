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
}
