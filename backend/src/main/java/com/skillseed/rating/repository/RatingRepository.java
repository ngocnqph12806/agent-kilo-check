package com.skillseed.rating.repository;

import com.skillseed.rating.domain.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Page<Rating> findByRateeId(UUID rateeId, Pageable pageable);

    Optional<Rating> findByBookingId(UUID bookingId);

    Optional<Rating> findByBookingIdAndRaterId(UUID bookingId, UUID raterId);

    List<Rating> findByBookingIdIn(List<UUID> bookingIds);

    /**
     * Count ratings submitted by a given rater so callers can decide whether a
     * rating already exists.
     */
    long countByBookingIdAndRaterId(UUID bookingId, UUID raterId);

    /**
     * Sum of {@code overall_score} for a ratee — denominator for the average.
     */
    @Query("SELECT COALESCE(SUM(r.overallScore), 0) FROM Rating r WHERE r.ratee.id = :rateeId AND r.overallScore IS NOT NULL")
    long sumOverallScoreByRatee(@Param("rateeId") UUID rateeId);

    /**
     * Count of distinct ratings received with a non-null overall_score —
     * denominator for the average.
     */
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratee.id = :rateeId AND r.overallScore IS NOT NULL")
    long countOverallScoreByRatee(@Param("rateeId") UUID rateeId);

    /**
     * Bookings that completed more than {@code cutoff} ago but are still missing
     * a rating from one or both participants — used by the auto-rate job.
     */
    @Query("""
            SELECT b.id FROM Booking b
            WHERE b.status IN (com.skillseed.shared.domain.BookingStatus.COMPLETED,
                               com.skillseed.shared.domain.BookingStatus.RATED)
              AND b.updatedAt < :cutoff
            """)
    List<UUID> findBookingIdsAwaitingRating(@Param("cutoff") Instant cutoff);
}
