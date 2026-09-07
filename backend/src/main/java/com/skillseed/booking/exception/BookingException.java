package com.skillseed.booking.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public BookingException(String code, String message, HttpStatus status) {
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