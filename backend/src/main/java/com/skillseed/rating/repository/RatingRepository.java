package com.skillseed.rating.repository;

import com.skillseed.rating.domain.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Page<Rating> findByRateeId(UUID rateeId, Pageable pageable);

    Optional<Rating> findByBookingId(UUID bookingId);
}
