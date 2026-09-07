package com.skillseed.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standard JSON error envelope returned by the API for all 4xx/5xx
 * responses. The {@code code} field is a stable identifier (e.g.
 * {@code INVALID_TOKEN}) that the frontend can switch on.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        int status,
        String code,
        String message,
        Instant timestamp) {

    public static ApiErrorResponse of(int status, String code, String message) {
        return new ApiErrorResponse(status, code, message, Instant.now());
    }
}