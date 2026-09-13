package com.skillseed.booking.exception;

import com.skillseed.shared.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Booking module domain exception.
 *
 * <p>Retained as a thin wrapper around {@link DomainException} for
 * backwards compatibility and to keep {@code throws} clauses expressive
 * (T-M411). Prefer throwing {@link DomainException} directly in new code.
 *
 * @deprecated since Sprint 6 — extend {@link DomainException} directly.
 */
@Deprecated
public class BookingException extends DomainException {

    public BookingException(String code, String message, HttpStatus status) {
        super(code, message, status);
    }

    public static BookingException badRequest(String code, String message) {
        return new BookingException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static BookingException notFound(String code, String message) {
        return new BookingException(code, message, HttpStatus.NOT_FOUND);
    }

    public static BookingException conflict(String code, String message) {
        return new BookingException(code, message, HttpStatus.CONFLICT);
    }

    public static BookingException forbidden(String code, String message) {
        return new BookingException(code, message, HttpStatus.FORBIDDEN);
    }

    public static BookingException unprocessable(String code, String message) {
        return new BookingException(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
