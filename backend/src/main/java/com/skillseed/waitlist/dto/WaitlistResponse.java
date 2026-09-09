package com.skillseed.waitlist.dto;

/**
 * Response returned after a successful waitlist signup. We deliberately
 * do not echo the email back to the client (privacy hygiene for marketing
 * forms) — only the position + a thank-you copy.
 */
public record WaitlistResponse(
        String message,
        int position
) {
}
