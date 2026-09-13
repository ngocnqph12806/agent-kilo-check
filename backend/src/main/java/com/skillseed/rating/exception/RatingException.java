package com.skillseed.rating.exception;

import com.skillseed.shared.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Rating module domain exception.
 *
 * <p>Retained as a thin wrapper around {@link DomainException} for
 * backwards compatibility and to keep {@code throws} clauses expressive
 * (T-M411). Prefer throwing {@link DomainException} directly in new code.
 *
 * @deprecated since Sprint 6 — extend {@link DomainException} directly.
 */
@Deprecated
public class RatingException extends DomainException {

    public RatingException(String code, String message, HttpStatus status) {
        super(code, message, status);
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
