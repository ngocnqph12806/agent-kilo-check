package com.skillseed.rating.job;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.rating.service.RatingService;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RatingAutoRateJobTest {

    private BookingRepository bookingRepository;
    private RatingService ratingService;
    private RatingAutoRateJob job;

    private static final UUID BOOKING_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID TEACHER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID LEARNER_ID = UUID.fromString("99999999-8888-7777-6666-555555555555");

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        ratingService = mock(RatingService.class);
        job = new RatingAutoRateJob(bookingRepository, ratingService, true, 7);
    }

    @Test
    void runSkipsWhenDisabled() {
        RatingAutoRateJob disabled = new RatingAutoRateJob(bookingRepository, ratingService, false, 7);
        disabled.run();
        verify(bookingRepository, never()).findByStatusAndUpdatedAtBefore(any(), any());
        verify(ratingService, never()).autoRateIfMissing(any());
    }

    @Test
    void runScansCompletedBookingsOlderThanGrace() {
        Booking booking = bookingFixture();
        when(bookingRepository.findByStatusAndUpdatedAtBefore(any(), any()))
                .thenReturn(List.of(booking));
        when(ratingService.autoRateIfMissing(booking)).thenReturn(true);

        job.run();

        ArgumentCaptor<Instant> cutoffCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(bookingRepository).findByStatusAndUpdatedAtBefore(
                org.mockito.ArgumentMatchers.eq(BookingStatus.COMPLETED),
                cutoffCaptor.capture());
        Instant expectedCutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        assertThat(cutoffCaptor.getValue()).isCloseTo(expectedCutoff,
                org.assertj.core.api.Assertions.within(5, ChronoUnit.SECONDS));
        verify(ratingService, times(1)).autoRateIfMissing(booking);
    }

    @Test
    void runSwallowsPerBookingFailures() {
        Booking good = bookingFixture();
        Booking bad = bookingFixture();
        when(bookingRepository.findByStatusAndUpdatedAtBefore(any(), any()))
                .thenReturn(List.of(good, bad));
        when(ratingService.autoRateIfMissing(good)).thenReturn(true);
        when(ratingService.autoRateIfMissing(bad)).thenThrow(new RuntimeException("boom"));

        job.run();

        verify(ratingService, times(1)).autoRateIfMissing(good);
        verify(ratingService, times(1)).autoRateIfMissing(bad);
    }

    @Test
    void constructorClampsGraceDaysToAtLeastOne() {
        RatingAutoRateJob j = new RatingAutoRateJob(bookingRepository, ratingService, true, 0);
        assertThat(j).extracting("grace").isNotNull();
    }

    private Booking bookingFixture() {
        User teacher = mock(User.class);
        when(teacher.getId()).thenReturn(TEACHER_ID);
        User learner = mock(User.class);
        when(learner.getId()).thenReturn(LEARNER_ID);
        Skill skill = mock(Skill.class);
        when(skill.getId()).thenReturn(UUID.randomUUID());

        Booking booking = new Booking(
                BOOKING_ID, teacher, learner, skill,
                Instant.now().minusSeconds(30L * 24 * 3600),
                (short) 30, 30);
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCreatedAt(Instant.now().minusSeconds(31L * 24 * 3600));
        try {
            Field f = Booking.class.getDeclaredField("updatedAt");
            f.setAccessible(true);
            f.set(booking, Instant.now().minusSeconds(8L * 24 * 3600));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
        return booking;
    }
}
