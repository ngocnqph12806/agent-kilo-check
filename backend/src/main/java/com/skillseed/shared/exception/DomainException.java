package com.skillseed.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Canonical domain exception for the whole backend (T-M411).
 *
 * <p>Carries a stable error {@code code} (consumed by the frontend) and the
 * desired HTTP status. Mapped by
 * {@link GlobalExceptionHandler#handleDomain(DomainException)} onto an
 * {@link ApiErrorResponse} payload.
 *
 * <p>Module-specific exceptions (AuthException, UserException, SkillException,
 * BookingException, SessionException, WalletException, RatingException) all
 * {@code extend} this class so a single handler can serve them. The module
 * subclasses are retained as thin wrappers for backwards compatibility and
 * expressive {@code throws} clauses, and are marked {@link Deprecated} for
 * removal once call sites migrate.
 *
 * <p>Factory methods map 1:1 to {@link HttpStatus} families most commonly
 * thrown by the domain layer.
 */
public class DomainException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public DomainException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    /**
     * Convenience constructor for legacy {@code int} status codes. Prefer
     * the {@link HttpStatus} overload in new code.
     */
    public DomainException(String code, String message, int status) {
        this(code, message, HttpStatus.valueOf(status));
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Legacy accessor for callers that still expect the {@code int} form.
     */
    public int getHttpStatus() {
        return status.value();
    }

    public static DomainException badRequest(String code, String message) {
        return new DomainException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static DomainException unauthorized(String code, String message) {
        return new DomainException(code, message, HttpStatus.UNAUTHORIZED);
    }

    public static DomainException forbidden(String code, String message) {
        return new DomainException(code, message, HttpStatus.FORBIDDEN);
    }

    public static DomainException notFound(String code, String message) {
        return new DomainException(code, message, HttpStatus.NOT_FOUND);
    }

    public static DomainException conflict(String code, String message) {
        return new DomainException(code, message, HttpStatus.CONFLICT);
    }

    public static DomainException unprocessable(String code, String message) {
        return new DomainException(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static DomainException tooManyRequests(String code, String message) {
        return new DomainException(code, message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
