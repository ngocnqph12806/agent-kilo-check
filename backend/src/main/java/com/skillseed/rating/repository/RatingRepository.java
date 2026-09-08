package com.skillseed.rating.repository;

import com.skillseed.rating.domain.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
