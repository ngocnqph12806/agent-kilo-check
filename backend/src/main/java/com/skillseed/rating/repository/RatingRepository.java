package com.skillseed.rating.repository;

import com.skillseed.rating.domain.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Page<Rating> findByRateeId(UUID rateeId, Pageable pageable);

    List<Rating> findByRaterId(UUID raterId);

    Optional<Rating> findByBookingIdAndRaterId(UUID bookingId, UUID raterId);

    long countByRateeId(UUID rateeId);

    long countByRateeIdAndOverallScoreIsNotNull(UUID rateeId);

    List<Rating> findByRateeIdAndOverallScoreIsNotNull(UUID rateeId);

    boolean existsByBookingId(UUID bookingId);

    /**
     * Aggregate average for a ratee. Null when the ratee has no scored
     * ratings yet; callers must guard. Replaces the old findAll +
     * in-memory sum/divide which loaded every rating row into memory
     * just to discard them.
     *
     * <p>JPQL {@code AVG()} returns {@link Double} — callers needing a
     * {@link java.math.BigDecimal} should convert via
     * {@code BigDecimal.valueOf(avg)}.
     */
    @Query("SELECT AVG(r.overallScore) FROM Rating r "
            + "WHERE r.ratee.id = :rateeId AND r.overallScore IS NOT NULL")
    Double averageOverallScoreForRatee(@Param("rateeId") UUID rateeId);
}
