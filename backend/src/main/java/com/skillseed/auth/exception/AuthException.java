package com.skillseed.auth.exception;

/**
 * Domain exception for authentication/authorization failures. Carries a
 * stable error {@code code} (consumed by frontend) and the desired
 * HTTP status, mapped by {@link com.skillseed.shared.exception.GlobalExceptionHandler}.
 */
public class AuthException extends RuntimeException {

    private final String code;
    private final int httpStatus;

    public AuthException(String code, String message, int httpStatus) {
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

    public static AuthException badRequest(String code, String message) {
        return new AuthException(code, message, 400);
    }

    public static AuthException unauthorized(String code, String message) {
        return new AuthException(code, message, 401);
    }

    public static AuthException conflict(String code, String message) {
        return new AuthException(code, message, 409);
    }

    public static AuthException tooManyRequests(String code, String message) {
        return new AuthException(code, message, 429);
    }
}