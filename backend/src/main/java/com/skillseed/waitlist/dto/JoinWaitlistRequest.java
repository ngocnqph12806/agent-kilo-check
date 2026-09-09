package com.skillseed.waitlist.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Public signup payload for the marketing waitlist. Source + referrer
 * are optional metadata so we can attribute conversions in the future.
 */
public record JoinWaitlistRequest(
        @NotBlank
        @Email(message = "Enter a valid email address")
        @Size(max = 255)
        String email,

        @Size(max = 40)
        @Pattern(regexp = "^[a-z0-9_-]+$",
                 message = "source must be lowercase kebab-case (or omitted)")
        String source,

        @Size(max = 255)
        String referrer
) {
}
