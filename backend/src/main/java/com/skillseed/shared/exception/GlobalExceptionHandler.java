package com.skillseed.shared.exception;

import com.skillseed.auth.exception.AuthException;
import com.skillseed.booking.exception.BookingException;
import com.skillseed.rating.exception.RatingException;
import com.skillseed.session.exception.SessionException;
import com.skillseed.skill.exception.SkillException;
import com.skillseed.user.exception.UserException;
import com.skillseed.wallet.exception.WalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Maps application exceptions to {@link ApiErrorResponse} JSON payloads with
 * stable error codes that the frontend can switch on.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiErrorResponse.of(ex.getHttpStatus(), ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiErrorResponse> handleUser(UserException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiErrorResponse.of(ex.getHttpStatus(), ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(WalletException.class)
    public ResponseEntity<ApiErrorResponse> handleWallet(WalletException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiErrorResponse.of(ex.getHttpStatus(), ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(SkillException.class)
    public ResponseEntity<ApiErrorResponse> handleSkill(SkillException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiErrorResponse.of(ex.getHttpStatus(), ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ApiErrorResponse> handleBooking(BookingException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiErrorResponse.of(ex.getStatus().value(), ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(SessionException.class)
    public ResponseEntity<ApiErrorResponse> handleSession(SessionException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiErrorResponse.of(ex.getStatus().value(), ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(RatingException.class)
    public ResponseEntity<ApiErrorResponse> handleRating(RatingException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiErrorResponse.of(ex.getStatus().value(), ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(400, "VALIDATION_FAILED", message));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of(401, "UNAUTHENTICATED", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of(403, "FORBIDDEN", "Access denied"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(400, "BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAny(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(500, "INTERNAL_ERROR", "Unexpected error"));
    }

    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}