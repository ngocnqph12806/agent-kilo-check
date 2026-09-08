package com.skillseed.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email(message = "email must be a valid address")
        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, max = 128, message = "password must be 8-128 characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "password must contain at least one letter and one digit")
        String password,

        @NotBlank(message = "fullName is required")
        @Size(max = 255, message = "fullName must be at most 255 characters")
        String fullName,

        @Size(max = 20, message = "phone must be at most 20 characters")
        String phone,

        @Size(min = 2, max = 2, message = "countryCode must be ISO-3166-1 alpha-2")
        String countryCode,

        @Size(max = 50, message = "timezone must be at most 50 characters")
        String timezone,

        @Size(max = 64, message = "inviteCode must be at most 64 characters")
        String inviteCode) {

    /** Convenience constructor used by tests and callers that only supply
     *  the minimum required registration fields. */
    public RegisterRequest(String email, String password, String fullName) {
        this(email, password, fullName, null, null, null, null);
    }
}
