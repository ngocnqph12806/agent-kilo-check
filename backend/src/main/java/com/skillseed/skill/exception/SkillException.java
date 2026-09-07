package com.skillseed.skill.exception;

public class SkillException extends RuntimeException {

    private final String code;
    private final int httpStatus;

    public SkillException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public static SkillException notFound(String code, String message) {
        return new SkillException(code, message, 404);
    }

    public static SkillException conflict(String code, String message) {
        return new SkillException(code, message, 409);
    }

    public static SkillException badRequest(String code, String message) {
        return new SkillException(code, message, 400);
    }
}