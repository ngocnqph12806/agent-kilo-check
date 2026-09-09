package com.skillseed.booking.service;

import com.skillseed.booking.exception.BookingException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link BookingService#calculateSeedAmount(int)} covering
 * the pricing tiers per {@code screens-svg/04-booking/01-booking-modal.svg:62-80}.
 *
 * <p>Locked-in by Sprint 5 (T-M400) when the 90-minute tier was added.
 */
class BookingServiceSeedAmountTest {

    @Test
    void fifteenAndThirtyMinuteSessionsCostOneSeed() {
        assertThat(BookingService.calculateSeedAmount(15)).isEqualTo(1);
        assertThat(BookingService.calculateSeedAmount(30)).isEqualTo(1);
    }

    @Test
    void fortyFiveMinuteSessionCostsTwoSeeds() {
        assertThat(BookingService.calculateSeedAmount(45)).isEqualTo(2);
    }

    @Test
    void sixtyMinuteSessionCostsThreeSeeds() {
        assertThat(BookingService.calculateSeedAmount(60)).isEqualTo(3);
    }

    @Test
    void ninetyMinuteSessionCostsFourSeeds() {
        assertThat(BookingService.calculateSeedAmount(90)).isEqualTo(4);
    }

    @Test
    void unsupportedDurationRaisesInvalidDurationError() {
        assertThatThrownBy(() -> BookingService.calculateSeedAmount(10))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("Duration must be one of")
                .extracting("code").isEqualTo("INVALID_DURATION");

        assertThatThrownBy(() -> BookingService.calculateSeedAmount(120))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("Duration must be one of")
                .extracting("code").isEqualTo("INVALID_DURATION");
    }
}
