package com.skillseed.user.dto;

import java.time.Instant;

/**
 * Materialised availability slot returned by
 * {@code GET /api/v1/users/{id}/availability}. Each slot is an
 * absolute UTC interval ready for the FE to render in a booking widget.
 *
 * @param startsAt    slot start, UTC
 * @param endsAt      slot end, UTC
 * @param timezone    the timezone the slot was rendered in (IANA)
 * @param sourceDow   day-of-week the slot originated from (0–6)
 */
public record FreeSlotResponse(
        Instant startsAt,
        Instant endsAt,
        String timezone,
        int sourceDow) {
}
