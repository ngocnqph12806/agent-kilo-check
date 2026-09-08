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
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

/**
 * Application service for the rating module.
 *
 * <p>Enforces the rules from the Sprint 3 spec:
 * <ul>
 *   <li>Booking must be {@link BookingStatus#COMPLETED}.</li>
 *   <li>Rater must be a participant (teacher or learner) on the booking.</li>
 *   <li>Each participant can submit at most one rating per booking
 *       (enforced by UNIQUE(booking_id, rater_id)).</li>
 *   <li>After a rating is created we recompute the ratee's
 *       {@code rating_avg} and increment {@code sessions_completed}.</li>
 * </ul>
 *
 * <p>The package-private {@link #autoRateIfMissing(Booking)} hook is
 * called by the scheduled job in Sprint 3 task T-M173.
 */
@Service
public class RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingService.class);
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository,
                         BookingRepository bookingRepository,
                         UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RatingResponse create(UUID actorId, CreateRatingRequest req) {
        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> RatingException.notFound("BOOKING_NOT_FOUND",
                        "Booking " + req.getBookingId() + " not found"));

        UUID teacherId = booking.getTeacher().getId();
        UUID learnerId = booking.getLearner().getId();
        if (!actorId.equals(teacherId) && !actorId.equals(learnerId)) {
            throw RatingException.forbidden("NOT_PARTICIPANT",
                    "You are not a participant of this booking");
        }
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw RatingException.unprocessable("BOOKING_NOT_COMPLETED",
                    "Booking must be COMPLETED to rate");
        }
        ratingRepository.findByBookingIdAndRaterId(booking.getId(), actorId)
                .ifPresent(existing -> {
                    throw RatingException.conflict("RATING_EXISTS",
                            "You have already rated this booking");
                });

        User rater = userRepository.findById(actorId)
                .orElseThrow(() -> RatingException.notFound("USER_NOT_FOUND",
                        "User " + actorId + " not found"));
        User ratee = userRepository.findById(actorId.equals(teacherId) ? learnerId : teacherId)
                .orElseThrow(() -> RatingException.notFound("USER_NOT_FOUND",
                        "Counterparty not found"));

        Rating rating = new Rating(UUID.randomUUID(), booking, rater, ratee);
        rating.setOverallScore(req.getOverallScore());
        rating.setReviewText(req.getReviewText());
        rating.setHelpfulnessScore(req.getHelpfulnessScore());
        rating.setKnowledgeScore(req.getKnowledgeScore());
        rating.setClarityScore(req.getClarityScore());
        rating.setPunctualityScore(req.getPunctualityScore());
        rating.setFriendlinessScore(req.getFriendlinessScore());
        if (req.getWouldRecommend() != null) {
            rating.setWouldRecommend(req.getWouldRecommend());
        }
        rating.setAutoRated(false);
        rating.setCreatedAt(Instant.now());

        ratingRepository.save(rating);
        recomputeRateeStats(ratee.getId());
        markBookingRatedIfBothPartiesRated(booking);

        log.info("Rating created: booking={} rater={} ratee={} score={}",
                booking.getId(), actorId, ratee.getId(), req.getOverallScore());

        return RatingResponse.from(rating, rater.getFullName());
    }

    /**
     * Flips the booking status to {@link BookingStatus#RATED} once
     * <em>both</em> participants have a row in {@code ratings} for
     * this booking. Idempotent: subsequent ratings on an already
     * RATED booking are still allowed (for historical records) but
     * no longer mutate status.
     */
    private void markBookingRatedIfBothPartiesRated(Booking booking) {
        if (booking.getStatus() == BookingStatus.RATED) {
            return;
        }
        UUID teacherId = booking.getTeacher().getId();
        UUID learnerId = booking.getLearner().getId();
        boolean teacherRated = ratingRepository
                .findByBookingIdAndRaterId(booking.getId(), teacherId).isPresent();
        boolean learnerRated = ratingRepository
                .findByBookingIdAndRaterId(booking.getId(), learnerId).isPresent();
        if (teacherRated && learnerRated) {
            booking.setStatus(BookingStatus.RATED);
            booking.setUpdatedAt(Instant.now());
            bookingRepository.save(booking);
            log.info("Booking {} flipped to RATED (both parties rated)", booking.getId());
        }
    }

    /**
     * Insert a synthetic 5-star rating if the {@code actor} has not yet
     * rated {@code booking}. Used by the scheduled auto-rate job and by
     * tests. Idempotent — calling twice is a no-op.
     */
    @Transactional
    public boolean autoRateIfMissing(Booking booking) {
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            return false;
        }
        UUID learnerId = booking.getLearner().getId();
        if (ratingRepository.findByBookingIdAndRaterId(booking.getId(), learnerId).isPresent()) {
            return false;
        }
        User learner = booking.getLearner();
        User teacher = booking.getTeacher();
        Rating rating = new Rating(UUID.randomUUID(), booking, learner, teacher);
        rating.setOverallScore((short) 5);
        rating.setReviewText(null);
        rating.setHelpfulnessScore((short) 5);
        rating.setRespectfulnessScore((short) 5);
        rating.setAutoRated(true);
        rating.setCreatedAt(Instant.now());
        ratingRepository.save(rating);
        recomputeRateeStats(teacher.getId());
        markBookingRatedIfBothPartiesRated(booking);
        log.info("Auto-rated booking {} (learner→teacher 5⭐)", booking.getId());
        return true;
    }

    @Transactional(readOnly = true)
    public RatingPageResponse listForUser(UUID userId, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Rating> result = ratingRepository.findByRateeId(userId, pageable);
        return new RatingPageResponse(
                result.getContent().stream()
                        .map(r -> RatingResponse.from(r, nameOrNull(r.getRater())))
                        .toList(),
                safePage,
                safeSize,
                result.getTotalElements(),
                result.getTotalPages());
    }

    private void recomputeRateeStats(UUID rateeId) {
        User ratee = userRepository.findById(rateeId).orElse(null);
        if (ratee == null) {
            return;
        }
        // Single aggregate query — no row materialisation. Old code loaded
        // every rating row into memory just to sum a column.
        BigDecimal newAvg = ratingRepository.averageOverallScoreForRatee(rateeId);
        if (newAvg != null) {
            ratee.setRatingAvg(newAvg.setScale(2, RoundingMode.HALF_UP));
        }
        ratee.setSessionsCompleted(ratee.getSessionsCompleted() + 1);
        userRepository.save(ratee);
    }

    private static String nameOrNull(User u) {
        return u == null ? null : u.getFullName();
    }
}
