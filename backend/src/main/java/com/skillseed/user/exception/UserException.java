package com.skillseed.user.exception;

import com.skillseed.shared.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * User module domain exception.
 *
 * <p>Retained as a thin wrapper around {@link DomainException} for
 * backwards compatibility and to keep {@code throws} clauses expressive
 * (T-M411). Prefer throwing {@link DomainException} directly in new code.
 *
 * @deprecated since Sprint 6 — extend {@link DomainException} directly.
 */
@Deprecated
public class UserException extends DomainException {

    public UserException(String code, String message, int httpStatus) {
        super(code, message, httpStatus);
    }

    public UserException(String code, String message, HttpStatus status) {
        super(code, message, status);
    }

    public static UserException notFound(String code, String message) {
        return new UserException(code, message, HttpStatus.NOT_FOUND);
    }

    public static UserException badRequest(String code, String message) {
        return new UserException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static UserException conflict(String code, String message) {
        return new UserException(code, message, HttpStatus.CONFLICT);
    }
}
