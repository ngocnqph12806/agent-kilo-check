package com.skillseed.shared.domain;

public enum SkillCategory {
    TECH("tech"),
    BUSINESS("business"),
    ART("art"),
    LANGUAGE("language"),
    LIFE("life"),
    HEALTH("health"),
    MUSIC("music"),
    SPORT("sport");

    private final String dbValue;

    SkillCategory(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
