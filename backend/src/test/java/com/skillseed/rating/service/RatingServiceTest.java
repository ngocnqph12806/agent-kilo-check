package com.skillseed.rating.service;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.rating.domain.Rating;
import com.skillseed.rating.dto.CreateRatingRequest;
import com.skillseed.rating.dto.RatingPageResponse;
import com.skillseed.rating.dto.RatingResponse;
import com.skillseed.rating.exception.RatingException;
import com.skillseed.rating.repository.RatingRepository;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RatingServiceTest {

    private RatingRepository ratingRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private RatingService service;

    private static final UUID BOOKING_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID TEACHER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID LEARNER_ID = UUID.fromString("99999999-8888-7777-6666-555555555555");

    @BeforeEach
    void setUp() {
        ratingRepository = mock(RatingRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        service = new RatingService(ratingRepository, bookingRepository, userRepository);
    }

    @Test
    void createRejectsNonParticipant() {
        Booking booking = bookingFixture(BookingStatus.COMPLETED, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        CreateRatingRequest req = request(BOOKING_ID, (short) 5);
        UUID bystander = UUID.randomUUID();

        assertThatThrownBy(() -> service.create(bystander, req))
                .isInstanceOf(RatingException.class)
                .satisfies(ex -> assertThat(((RatingException) ex).getCode())
                        .isEqualTo("NOT_PARTICIPANT"));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void createRejectsNonCompletedBooking() {
        Booking booking = bookingFixture(BookingStatus.IN_PROGRESS, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.create(LEARNER_ID, request(BOOKING_ID, (short) 5)))
                .isInstanceOf(RatingException.class)
                .satisfies(ex -> assertThat(((RatingException) ex).getCode())
                        .isEqualTo("BOOKING_NOT_COMPLETED"));
    }

    @Test
    void createPersistsRatingAndUpdatesRateeStats() {
        Booking booking = bookingFixture(BookingStatus.COMPLETED, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(ratingRepository.findByBookingIdAndRaterId(BOOKING_ID, LEARNER_ID))
                .thenReturn(Optional.empty());
        when(userRepository.findById(LEARNER_ID))
                .thenReturn(Optional.of(user(LEARNER_ID, "Aria", 0)));
        when(userRepository.findById(TEACHER_ID))
                .thenReturn(Optional.of(user(TEACHER_ID, "Tom", 3)));
        when(ratingRepository.countByRateeIdAndOverallScoreIsNotNull(TEACHER_ID)).thenReturn(4L);
        Rating existing = mock(Rating.class);
        when(existing.getOverallScore()).thenReturn((short) 5);
        when(ratingRepository.findByRateeIdAndOverallScoreIsNotNull(TEACHER_ID))
                .thenReturn(List.of(existing, existing, existing, existing));

        RatingResponse response = service.create(LEARNER_ID,
                request(BOOKING_ID, (short) 5));

        assertThat(response.getBookingId()).isEqualTo(BOOKING_ID);
        assertThat(response.getRateeId()).isEqualTo(TEACHER_ID);
        assertThat(response.getRaterName()).isEqualTo("Aria");

        ArgumentCaptor<Rating> savedRating = ArgumentCaptor.forClass(Rating.class);
        verify(ratingRepository).save(savedRating.capture());
        assertThat(savedRating.getValue().getOverallScore()).isEqualTo((short) 5);
        assertThat(savedRating.getValue().isAutoRated()).isFalse();

        ArgumentCaptor<User> savedTeacher = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedTeacher.capture());
        assertThat(savedTeacher.getValue().getRatingAvg()).isEqualByComparingTo("5.0");
        assertThat(savedTeacher.getValue().getSessionsCompleted()).isEqualTo(4);
    }

    @Test
    void createRejectsDuplicateRating() {
        Booking booking = bookingFixture(BookingStatus.COMPLETED, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        Rating existing = new Rating(UUID.randomUUID(), booking,
                user(LEARNER_ID, "Aria", 0), user(TEACHER_ID, "Tom", 0));
        when(ratingRepository.findByBookingIdAndRaterId(BOOKING_ID, LEARNER_ID))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.create(LEARNER_ID, request(BOOKING_ID, (short) 4)))
                .isInstanceOf(RatingException.class)
                .satisfies(ex -> assertThat(((RatingException) ex).getCode())
                        .isEqualTo("RATING_EXISTS"));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void autoRateIfMissingInsertsFiveStarRating() {
        Booking booking = bookingFixture(BookingStatus.COMPLETED, TEACHER_ID, LEARNER_ID);
        when(ratingRepository.findByBookingIdAndRaterId(BOOKING_ID, LEARNER_ID))
                .thenReturn(Optional.empty());
        when(ratingRepository.countByRateeIdAndOverallScoreIsNotNull(TEACHER_ID)).thenReturn(0L);

        boolean inserted = service.autoRateIfMissing(booking);

        assertThat(inserted).isTrue();
        ArgumentCaptor<Rating> saved = ArgumentCaptor.forClass(Rating.class);
        verify(ratingRepository).save(saved.capture());
        assertThat(saved.getValue().getOverallScore()).isEqualTo((short) 5);
        assertThat(saved.getValue().isAutoRated()).isTrue();
    }

    @Test
    void autoRateIfMissingIsNoopWhenAlreadyRated() {
        Booking booking = bookingFixture(BookingStatus.COMPLETED, TEACHER_ID, LEARNER_ID);
        when(ratingRepository.findByBookingIdAndRaterId(BOOKING_ID, LEARNER_ID))
                .thenReturn(Optional.of(mock(Rating.class)));

        boolean inserted = service.autoRateIfMissing(booking);

        assertThat(inserted).isFalse();
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void autoRateIfMissingSkipsNonCompletedBooking() {
        Booking booking = bookingFixture(BookingStatus.CONFIRMED, TEACHER_ID, LEARNER_ID);
        boolean inserted = service.autoRateIfMissing(booking);
        assertThat(inserted).isFalse();
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void listForUserReturnsPagedDto() {
        Booking booking = bookingFixture(BookingStatus.COMPLETED, TEACHER_ID, LEARNER_ID);
        Rating rating = new Rating(UUID.randomUUID(), booking,
                user(LEARNER_ID, "Aria", 0), user(TEACHER_ID, "Tom", 0));
        rating.setOverallScore((short) 5);
        rating.setCreatedAt(Instant.now());

        Page<Rating> page = new PageImpl<>(List.of(rating), PageRequest.of(0, 20), 1);
        when(ratingRepository.findByRateeId(eq(TEACHER_ID), any(Pageable.class))).thenReturn(page);

        RatingPageResponse response = service.listForUser(TEACHER_ID, 0, 20);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRaterName()).isEqualTo("Aria");
        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getTotalPages()).isEqualTo(1);
    }

    @Test
    void listForUserClampsPageSize() {
        Booking booking = bookingFixture(BookingStatus.COMPLETED, TEACHER_ID, LEARNER_ID);
        Page<Rating> page = new PageImpl<>(List.of(), PageRequest.of(0, 100), 0);
        when(ratingRepository.findByRateeId(eq(TEACHER_ID), any(Pageable.class))).thenReturn(page);

        RatingPageResponse response = service.listForUser(TEACHER_ID, -5, 9999);

        assertThat(response.getSize()).isEqualTo(100);
        assertThat(response.getPage()).isEqualTo(0);
        verify(ratingRepository).findByRateeId(eq(TEACHER_ID),
                any(Pageable.class));
    }

    private Booking bookingFixture(BookingStatus status, UUID teacherId, UUID learnerId) {
        User teacher = mock(User.class);
        when(teacher.getId()).thenReturn(teacherId);
        when(teacher.getFullName()).thenReturn("Teacher");

        User learner = mock(User.class);
        when(learner.getId()).thenReturn(learnerId);
        when(learner.getFullName()).thenReturn("Learner");

        Skill skill = mock(Skill.class);
        when(skill.getId()).thenReturn(UUID.randomUUID());

        Booking booking = new Booking(
                BOOKING_ID, teacher, learner, skill,
                Instant.now().minusSeconds(3600), (short) 30, 30);
        booking.setStatus(status);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());
        return booking;
    }

    private User user(UUID id, String name, int sessionsCompleted) {
        User u = new User(id, id + "@example.com", name);
        u.setSessionsCompleted(sessionsCompleted);
        try {
            Field f = User.class.getDeclaredField("ratingAvg");
            f.setAccessible(true);
            f.set(u, new java.math.BigDecimal("0.0"));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
        return u;
    }

    private CreateRatingRequest request(UUID bookingId, Short score) {
        CreateRatingRequest req = new CreateRatingRequest();
        req.setBookingId(bookingId);
        req.setOverallScore(score);
        req.setReviewText("Great session");
        req.setHelpfulnessScore(score);
        req.setRespectfulnessScore(score);
        return req;
    }
}
