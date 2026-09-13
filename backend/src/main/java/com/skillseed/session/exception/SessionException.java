package com.skillseed.session.exception;

import com.skillseed.shared.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Session module domain exception.
 *
 * <p>Retained as a thin wrapper around {@link DomainException} for
 * backwards compatibility and to keep {@code throws} clauses expressive
 * (T-M411). Prefer throwing {@link DomainException} directly in new code.
 *
 * @deprecated since Sprint 6 — extend {@link DomainException} directly.
 */
@Deprecated
public class SessionException extends DomainException {

    public SessionException(String code, String message, HttpStatus status) {
        super(code, message, status);
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
