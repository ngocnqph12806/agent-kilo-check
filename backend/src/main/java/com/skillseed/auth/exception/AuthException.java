package com.skillseed.auth.exception;

import com.skillseed.shared.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Authentication / authorization domain exception.
 *
 * <p>Retained as a thin wrapper around {@link DomainException} for
 * backwards compatibility and to keep {@code throws} clauses expressive
 * (T-M411). Prefer throwing {@link DomainException} directly in new code.
 *
 * @deprecated since Sprint 6 — extend {@link DomainException} directly.
 */
@Deprecated
public class AuthException extends DomainException {

    public AuthException(String code, String message, int httpStatus) {
        super(code, message, httpStatus);
    }

    public AuthException(String code, String message, HttpStatus status) {
        super(code, message, status);
    }

    public static AuthException badRequest(String code, String message) {
        return new AuthException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static AuthException unauthorized(String code, String message) {
        return new AuthException(code, message, HttpStatus.UNAUTHORIZED);
    }

    public static AuthException conflict(String code, String message) {
        return new AuthException(code, message, HttpStatus.CONFLICT);
    }

    public static AuthException tooManyRequests(String code, String message) {
        return new AuthException(code, message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
