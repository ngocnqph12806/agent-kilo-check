package com.skillseed.rating.exception;

import org.springframework.http.HttpStatus;

public class RatingException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public RatingException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static RatingException badRequest(String code, String message) {
        return new RatingException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static RatingException notFound(String code, String message) {
        return new RatingException(code, message, HttpStatus.NOT_FOUND);
    }

    public static RatingException conflict(String code, String message) {
        return new RatingException(code, message, HttpStatus.CONFLICT);
    }

    public static RatingException forbidden(String code, String message) {
        return new RatingException(code, message, HttpStatus.FORBIDDEN);
    }

    public static RatingException unprocessable(String code, String message) {
        return new RatingException(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
