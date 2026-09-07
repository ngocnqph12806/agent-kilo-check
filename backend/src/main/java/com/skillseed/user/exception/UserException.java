package com.skillseed.user.exception;

public class UserException extends RuntimeException {

    private final String code;
    private final int httpStatus;

    public UserException(String code, String message, int httpStatus) {
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

    public static UserException notFound(String code, String message) {
        return new UserException(code, message, 404);
    }

    public static UserException badRequest(String code, String message) {
        return new UserException(code, message, 400);
    }

    public static UserException conflict(String code, String message) {
        return new UserException(code, message, 409);
    }
}