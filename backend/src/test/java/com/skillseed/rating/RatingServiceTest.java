package com.skillseed.rating;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.notification.service.NotificationService;
import com.skillseed.rating.domain.Rating;
import com.skillseed.rating.dto.CreateRatingRequest;
import com.skillseed.rating.dto.RatingPageResponse;
import com.skillseed.rating.dto.RatingResponse;
import com.skillseed.rating.exception.RatingException;
import com.skillseed.rating.repository.RatingRepository;
import com.skillseed.rating.service.RatingService;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.domain.SkillCategory;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService(ratingRepository, bookingRepository,
                userRepository, notificationService);
    }

    @Test
    void submit_learnerToTeacher_persistsOverallAndRefreshesAggregates() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(ratingRepository.countByBookingIdAndRaterId(booking.getId(), learnerId)).thenReturn(0L);
        when(userRepository.findById(learnerId)).thenReturn(Optional.of(stubUser(learnerId, "Bob")));
        when(ratingRepository.sumOverallScoreByRatee(teacherId)).thenReturn(20L);
        when(ratingRepository.countOverallScoreByRatee(teacherId)).thenReturn(5L);
        when(ratingRepository.countByBookingIdAndRaterId(booking.getId(), learnerId))
                .thenReturn(1L)
                .thenReturn(1L);
        when(ratingRepository.countByBookingIdAndRaterId(booking.getId(), teacherId)).thenReturn(0L);

        RatingResponse response = ratingService.submit(learnerId,
                new CreateRatingRequest(booking.getId(), (short) 4, null, null, "Great session"));

        assertThat(response.direction()).isEqualTo("learner_to_teacher");
        assertThat(response.overallScore()).isEqualTo((short) 4);
        assertThat(response.reviewText()).isEqualTo("Great session");

        ArgumentCaptor<Rating> saved = ArgumentCaptor.forClass(Rating.class);
        verify(ratingRepository).save(saved.capture());
        assertThat(saved.getValue().getOverallScore()).isEqualTo((short) 4);

        ArgumentCaptor<User> teacherSaved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(teacherSaved.capture());
        assertThat(teacherSaved.getValue().getRatingAvg()).isEqualByComparingTo(BigDecimal.valueOf(4.0));
        assertThat(teacherSaved.getValue().getSessionsCompleted()).isEqualTo(1);
    }

    @Test
    void submit_teacherToLearner_persistsHelpfulnessRespectfulness() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(ratingRepository.countByBookingIdAndRaterId(booking.getId(), teacherId)).thenReturn(0L);
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(stubUser(teacherId, "Alice")));

        RatingResponse response = ratingService.submit(teacherId,
                new CreateRatingRequest(booking.getId(), null, (short) 5, (short) 5, null));

        assertThat(response.direction()).isEqualTo("teacher_to_learner");
        assertThat(response.helpfulnessScore()).isEqualTo((short) 5);
        assertThat(response.respectfulnessScore()).isEqualTo((short) 5);
        verify(userRepository, never()).save(any());
    }

    @Test
    void submit_rejectsNonParticipant() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> ratingService.submit(UUID.randomUUID(),
                new CreateRatingRequest(booking.getId(), (short) 5, null, null, null)))
                .isInstanceOf(RatingException.class)
                .hasMessageContaining("NOT_PARTICIPANT");
    }

    @Test
    void submit_rejectsNonCompletedBooking() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> ratingService.submit(learnerId,
                new CreateRatingRequest(booking.getId(), (short) 5, null, null, null)))
                .isInstanceOf(RatingException.class)
                .hasMessageContaining("BOOKING_NOT_COMPLETED");
    }

    @Test
    void submit_rejectsDuplicateRating() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(ratingRepository.countByBookingIdAndRaterId(booking.getId(), learnerId)).thenReturn(1L);
        assertThatThrownBy(() -> ratingService.submit(learnerId,
                new CreateRatingRequest(booking.getId(), (short) 5, null, null, null)))
                .isInstanceOf(RatingException.class)
                .hasMessageContaining("RATING_DUPLICATE");
    }

    @Test
    void submit_learnerMissingOverallScore_isRejected() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(ratingRepository.countByBookingIdAndRaterId(booking.getId(), learnerId)).thenReturn(0L);
        when(userRepository.findById(learnerId)).thenReturn(Optional.of(stubUser(learnerId, "Bob")));
        assertThatThrownBy(() -> ratingService.submit(learnerId,
                new CreateRatingRequest(booking.getId(), null, null, null, null)))
                .isInstanceOf(RatingException.class)
                .hasMessageContaining("OVERALL_SCORE_REQUIRED");
    }

    @Test
    void submit_teacherMissingHelpfulnessScores_isRejected() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(ratingRepository.countByBookingIdAndRaterId(booking.getId(), teacherId)).thenReturn(0L);
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(stubUser(teacherId, "Alice")));
        assertThatThrownBy(() -> ratingService.submit(teacherId,
                new CreateRatingRequest(booking.getId(), null, (short) 5, null, null)))
                .isInstanceOf(RatingException.class)
                .hasMessageContaining("HELPFULNESS_RESPECTFULNESS_REQUIRED");
    }

    @Test
    void submit_marksBookingRatedOnceBothSidesRate() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(ratingRepository.countByBookingIdAndRaterId(eq(booking.getId()), eq(learnerId)))
                .thenReturn(0L);
        when(userRepository.findById(learnerId)).thenReturn(Optional.of(stubUser(learnerId, "Bob")));
        when(ratingRepository.sumOverallScoreByRatee(teacherId)).thenReturn(15L);
        when(ratingRepository.countOverallScoreByRatee(teacherId)).thenReturn(3L);
        when(ratingRepository.countByBookingIdAndRaterId(eq(booking.getId()), eq(teacherId)))
                .thenReturn(1L);

        ratingService.submit(learnerId,
                new CreateRatingRequest(booking.getId(), (short) 5, null, null, null));

        ArgumentCaptor<Booking> savedBooking = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository, times(1)).save(savedBooking.capture());
        assertThat(savedBooking.getValue().getStatus()).isEqualTo(BookingStatus.RATED);
    }

    @Test
    void listForUser_returnsAverageWhenRatingsExist() {
        UUID rateeId = UUID.randomUUID();
        Rating rating = new Rating(UUID.randomUUID(),
                newCompletedBooking(UUID.randomUUID(), UUID.randomUUID()),
                stubUser(UUID.randomUUID(), "Rater"),
                stubUser(rateeId, "Ratee"));
        rating.setOverallScore((short) 5);
        rating.setCreatedAt(Instant.now());
        Page<Rating> page = new PageImpl<>(List.of(rating), PageRequest.of(0, 20), 1);
        when(ratingRepository.findByRateeId(rateeId, PageRequest.of(0, 20))).thenReturn(page);
        when(ratingRepository.sumOverallScoreByRatee(rateeId)).thenReturn(20L);
        when(ratingRepository.countOverallScoreByRatee(rateeId)).thenReturn(5L);

        RatingPageResponse response = ratingService.listForUser(rateeId, 0, 20);

        assertThat(response.totalElements()).isEqualTo(1L);
        assertThat(response.averageOverallScore()).isEqualTo(4.0);
    }

    @Test
    void listForUser_returnsNullAverageWhenNoRatings() {
        UUID rateeId = UUID.randomUUID();
        when(ratingRepository.findByRateeId(eq(rateeId), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
        when(ratingRepository.sumOverallScoreByRatee(rateeId)).thenReturn(0L);
        when(ratingRepository.countOverallScoreByRatee(rateeId)).thenReturn(0L);

        RatingPageResponse response = ratingService.listForUser(rateeId, 0, 20);
        assertThat(response.averageOverallScore()).isNull();
    }

    @Test
    void autoRateStaleBookings_writesBothRowsAndMarksRated() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        UUID bookingId = booking.getId();
        when(ratingRepository.findBookingIdsAwaitingRating(any(Instant.class)))
                .thenReturn(List.of(bookingId));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        // First call (learner side) is 0 → writes; second call (teacher side) is 0 → writes.
        when(ratingRepository.countByBookingIdAndRaterId(bookingId, learnerId))
                .thenReturn(0L);
        when(ratingRepository.countByBookingIdAndRaterId(bookingId, teacherId))
                .thenReturn(0L);
        when(ratingRepository.sumOverallScoreByRatee(teacherId)).thenReturn(5L);
        when(ratingRepository.countOverallScoreByRatee(teacherId)).thenReturn(1L);

        int closed = ratingService.autoRateStaleBookings();

        assertThat(closed).isEqualTo(1);
        verify(ratingRepository, times(2)).save(any(Rating.class));
        ArgumentCaptor<Booking> savedBooking = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(savedBooking.capture());
        assertThat(savedBooking.getValue().getStatus()).isEqualTo(BookingStatus.RATED);
    }

    @Test
    void autoRateStaleBookings_skipsBookingsThatAlreadyHaveRatings() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newCompletedBooking(teacherId, learnerId);
        UUID bookingId = booking.getId();
        when(ratingRepository.findBookingIdsAwaitingRating(any(Instant.class)))
                .thenReturn(List.of(bookingId));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(ratingRepository.countByBookingIdAndRaterId(bookingId, learnerId)).thenReturn(1L);
        when(ratingRepository.countByBookingIdAndRaterId(bookingId, teacherId)).thenReturn(1L);

        int closed = ratingService.autoRateStaleBookings();
        assertThat(closed).isEqualTo(0);
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    private Booking newCompletedBooking(UUID teacherId, UUID learnerId) {
        User teacher = stubUser(teacherId, "Alice");
        User learner = stubUser(learnerId, "Bob");
        Skill skill = new Skill(UUID.randomUUID(), "java", "Java", SkillCategory.TECH);
        Booking booking = new Booking(UUID.randomUUID(), teacher, learner, skill,
                Instant.now().minusSeconds(86_400), (short) 30, 30);
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setUpdatedAt(Instant.now().minusSeconds(86_400));
        return booking;
    }

    private User stubUser(UUID id, String fullName) {
        User user = new User(id, fullName.toLowerCase() + "@example.com", fullName);
        user.setTimezone("UTC");
        return user;
    }
}
