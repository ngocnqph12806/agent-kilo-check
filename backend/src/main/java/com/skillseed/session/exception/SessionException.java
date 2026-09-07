package com.skillseed.session.exception;

import org.springframework.http.HttpStatus;

/**
 * Domain-specific exception for the video session module. Mapped to
 * HTTP responses by {@code GlobalExceptionHandler}.
 */
public class SessionException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public SessionException(HttpStatus status, String code, String message) {
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

    public static SessionException badRequest(String code, String message) {
        return new SessionException(HttpStatus.BAD_REQUEST, code, message);
    }

    public static SessionException forbidden(String code, String message) {
        return new SessionException(HttpStatus.FORBIDDEN, code, message);
    }

    public static SessionException notFound(String code, String message) {
        return new SessionException(HttpStatus.NOT_FOUND, code, message);
    }

    public static SessionException conflict(String code, String message) {
        return new SessionException(HttpStatus.CONFLICT, code, message);
    }

    public static SessionException unprocessable(String code, String message) {
        return new SessionException(HttpStatus.UNPROCESSABLE_ENTITY, code, message);
    }

    public static SessionException internal(String code, String message) {
        return new SessionException(HttpStatus.INTERNAL_SERVER_ERROR, code, message);
    }
}
