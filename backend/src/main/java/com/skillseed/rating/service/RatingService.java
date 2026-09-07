package com.skillseed.rating.service;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.notification.domain.NotificationType;
import com.skillseed.notification.service.NotificationService;
import com.skillseed.rating.domain.Rating;
import com.skillseed.rating.dto.CreateRatingRequest;
import com.skillseed.rating.dto.RatingPageResponse;
import com.skillseed.rating.dto.RatingResponse;
import com.skillseed.rating.exception.RatingException;
import com.skillseed.rating.repository.RatingRepository;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

/**
 * Rating module service (T-M170..T-M174).
 *
 * <p>Two ratings per booking — one per direction — backed by the unique
 * {@code (booking_id, rater_id)} index introduced in V6. After both
 * sides submit, the booking flips to {@code RATED} (FR-M64).
 *
 * <p>Denormalised aggregates on {@link User#getRatingAvg()} and
 * {@link User#getSessionsCompleted()} are recomputed whenever a new
 * teacher-side rating lands, so discover / profile views stay cheap.
 */
@Service
public class RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingService.class);
    static final Duration AUTO_RATE_GRACE = Duration.ofDays(7);
    static final short AUTO_RATE_SCORE = 5;

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public RatingService(RatingRepository ratingRepository,
                         BookingRepository bookingRepository,
                         UserRepository userRepository,
                         NotificationService notificationService) {
        this.ratingRepository = ratingRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public RatingResponse submit(UUID raterId, CreateRatingRequest req) {
        Booking booking = bookingRepository.findById(req.bookingId())
                .orElseThrow(() -> RatingException.notFound("BOOKING_NOT_FOUND",
                        "Booking not found"));

        boolean isLearner = raterId.equals(booking.getLearner().getId());
        boolean isTeacher = raterId.equals(booking.getTeacher().getId());
        if (!isLearner && !isTeacher) {
            throw RatingException.forbidden("NOT_PARTICIPANT",
                    "Only participants can rate this session");
        }

        BookingStatus status = booking.getStatus();
        if (status != BookingStatus.COMPLETED && status != BookingStatus.RATED) {
            throw RatingException.conflict("BOOKING_NOT_COMPLETED",
                    "Can only rate a completed session");
        }

        if (ratingRepository.countByBookingIdAndRaterId(booking.getId(), raterId) > 0) {
            throw RatingException.conflict("RATING_DUPLICATE",
                    "You have already rated this session");
        }

        User rater = userRepository.findById(raterId)
                .orElseThrow(() -> RatingException.notFound("USER_NOT_FOUND", "User not found"));
        User ratee = isLearner ? booking.getTeacher() : booking.getLearner();

        Rating rating = new Rating(UUID.randomUUID(), booking, rater, ratee);
        rating.setCreatedAt(Instant.now());

        if (isLearner) {
            if (req.overallScore() == null) {
                throw RatingException.badRequest("OVERALL_SCORE_REQUIRED",
                        "Learner must provide overallScore");
            }
            rating.setOverallScore(req.overallScore());
            rating.setReviewText(req.reviewText());
        } else {
            if (req.helpfulnessScore() == null || req.respectfulnessScore() == null) {
                throw RatingException.badRequest("HELPFULNESS_RESPECTFULNESS_REQUIRED",
                        "Teacher must provide helpfulnessScore and respectfulnessScore");
            }
            rating.setHelpfulnessScore(req.helpfulnessScore());
            rating.setRespectfulnessScore(req.respectfulnessScore());
            rating.setReviewText(req.reviewText());
        }
        rating = ratingRepository.save(rating);

        // If this is the learner→teacher rating, refresh the ratee's denormalised
        // aggregates (rating_avg + sessions_completed) so discover / profile
        // pages stay current.
        if (isLearner) {
            updateTeacherAggregates(ratee.getId(), booking.getId());
        }

        // Flip booking to RATED once both sides have submitted.
        maybeMarkBookingRated(booking);

        log.info("Rating submitted booking={} rater={} ratee={}",
                booking.getId(), rater.getId(), ratee.getId());
        return RatingResponse.from(rating);
    }

    @Transactional(readOnly = true)
    public RatingPageResponse listForUser(UUID rateeId, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? 20 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Rating> result = ratingRepository.findByRateeId(rateeId, pageable);

        long sum = ratingRepository.sumOverallScoreByRatee(rateeId);
        long count = ratingRepository.countOverallScoreByRatee(rateeId);
        Double avg = count == 0 ? null : (double) sum / count;

        List<RatingResponse> content = result.getContent().stream()
                .map(RatingResponse::from)
                .toList();
        return new RatingPageResponse(
                content,
                safePage,
                safeSize,
                result.getTotalElements(),
                result.getTotalPages(),
                avg);
    }

    @Transactional(readOnly = true)
    public Optional<RatingResponse> findMineForBooking(UUID bookingId, UUID raterId) {
        return ratingRepository.findByBookingIdAndRaterId(bookingId, raterId)
                .map(RatingResponse::from);
    }

    /**
     * Sweep bookings completed more than {@link #AUTO_RATE_GRACE} ago that
     * are missing one or both ratings and backfill them with the default
     * 5⭐ (FR-M65). Idempotent — re-running is a no-op once both rows exist.
     *
     * @return number of bookings fully closed by this pass.
     */
    @Transactional
    public int autoRateStaleBookings() {
        Instant cutoff = Instant.now().minus(AUTO_RATE_GRACE);
        List<UUID> candidateIds = ratingRepository.findBookingIdsAwaitingRating(cutoff);
        if (candidateIds.isEmpty()) {
            return 0;
        }

        int closed = 0;
        for (UUID bookingId : candidateIds) {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                continue;
            }
            boolean wroteLearnerRow = ensureAutoRow(booking, booking.getLearner(),
                    booking.getTeacher(), true);
            boolean wroteTeacherRow = ensureAutoRow(booking, booking.getTeacher(),
                    booking.getLearner(), false);
            if (wroteLearnerRow) {
                updateTeacherAggregates(booking.getTeacher().getId(), booking.getId());
            }
            maybeMarkBookingRated(booking);
            if (wroteLearnerRow || wroteTeacherRow) {
                closed++;
            }
        }
        if (closed > 0) {
            log.info("Auto-rated {} stale bookings older than {}", closed, AUTO_RATE_GRACE);
        }
        return closed;
    }

    private boolean ensureAutoRow(Booking booking, User rater, User ratee, boolean learnerSide) {
        if (ratingRepository.countByBookingIdAndRaterId(booking.getId(), rater.getId()) > 0) {
            return false;
        }
        Rating rating = new Rating(UUID.randomUUID(), booking, rater, ratee);
        rating.setCreatedAt(Instant.now());
        if (learnerSide) {
            rating.setOverallScore(AUTO_RATE_SCORE);
            rating.setReviewText("Auto-rated after 7 days without review");
        } else {
            rating.setHelpfulnessScore(AUTO_RATE_SCORE);
            rating.setRespectfulnessScore(AUTO_RATE_SCORE);
        }
        ratingRepository.save(rating);
        log.info("Auto-rated booking={} rater={}", booking.getId(), rater.getId());
        return true;
    }

    private void updateTeacherAggregates(UUID teacherId, UUID sourceBookingId) {
        User teacher = userRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return;
        }
        long sum = ratingRepository.sumOverallScoreByRatee(teacherId);
        long count = ratingRepository.countOverallScoreByRatee(teacherId);
        double avg = count == 0 ? 0.0 : (double) sum / count;
        teacher.setRatingAvg(BigDecimal.valueOf(round1(avg)));
        teacher.setSessionsCompleted(teacher.getSessionsCompleted() + 1);
        userRepository.save(teacher);
        log.debug("Updated teacher={} rating_avg={} sessions_completed={} after rating on booking={}",
                teacherId, teacher.getRatingAvg(), teacher.getSessionsCompleted(), sourceBookingId);
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private void maybeMarkBookingRated(Booking booking) {
        long learnerRow = ratingRepository.countByBookingIdAndRaterId(
                booking.getId(), booking.getLearner().getId());
        long teacherRow = ratingRepository.countByBookingIdAndRaterId(
                booking.getId(), booking.getTeacher().getId());
        if (learnerRow == 0 || teacherRow == 0) {
            return;
        }
        if (booking.getStatus() == BookingStatus.RATED) {
            return;
        }
        booking.setStatus(BookingStatus.RATED);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("bookingId", booking.getId().toString());
        notificationService.publish(booking.getLearner(), NotificationType.RATING_PROMPT, payload);
        notificationService.publish(booking.getTeacher(), NotificationType.RATING_PROMPT, payload);
    }

    /**
     * Notify both sides they can rate a freshly completed booking
     * (T-M172). Idempotent.
     */
    public void promptRating(Booking booking) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bookingId", booking.getId().toString());
        payload.put("scheduledAt", booking.getScheduledAt().toString());
        notificationService.publish(booking.getLearner(), NotificationType.RATING_PROMPT, payload);
        notificationService.publish(booking.getTeacher(), NotificationType.RATING_PROMPT, payload);
    }
}
