package com.skillseed.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "email must be a valid address")
        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "password is required")
        String password) {
}