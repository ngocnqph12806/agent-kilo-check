package com.skillseed.session.exception;

import org.springframework.http.HttpStatus;

/**
 * Domain exception thrown by the session module. Handled by
 * {@link com.skillseed.shared.exception.GlobalExceptionHandler}
 * which is wired to map it onto an {@code ApiErrorResponse}
 * payload with the embedded {@code code} field.
 */
public class SessionException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public SessionException(String code, String message, HttpStatus status) {
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

    public static SessionException notFound(String code, String message) {
        return new SessionException(code, message, HttpStatus.NOT_FOUND);
    }

    public static SessionException forbidden(String code, String message) {
        return new SessionException(code, message, HttpStatus.FORBIDDEN);
    }

    public static SessionException conflict(String code, String message) {
        return new SessionException(code, message, HttpStatus.CONFLICT);
    }

    public static SessionException badRequest(String code, String message) {
        return new SessionException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static SessionException unprocessable(String code, String message) {
        return new SessionException(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
