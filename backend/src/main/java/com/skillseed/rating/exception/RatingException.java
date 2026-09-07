package com.skillseed.rating.exception;

import org.springframework.http.HttpStatus;

public class RatingException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public RatingException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public static RatingException badRequest(String code, String message) {
        return new RatingException(HttpStatus.BAD_REQUEST, code, message);
    }

    public static RatingException forbidden(String code, String message) {
        return new RatingException(HttpStatus.FORBIDDEN, code, message);
    }

    public static RatingException notFound(String code, String message) {
        return new RatingException(HttpStatus.NOT_FOUND, code, message);
    }

    public static RatingException conflict(String code, String message) {
        return new RatingException(HttpStatus.CONFLICT, code, message);
    }
}
