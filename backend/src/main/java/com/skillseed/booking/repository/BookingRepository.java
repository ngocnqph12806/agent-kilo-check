package com.skillseed.booking.repository;

import com.skillseed.booking.domain.Booking;
import com.skillseed.shared.domain.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Page<Booking> findByTeacherIdAndStatusIn(UUID teacherId, List<BookingStatus> statuses, Pageable pageable);

    Page<Booking> findByLearnerIdAndStatusIn(UUID learnerId, List<BookingStatus> statuses, Pageable pageable);

    Page<Booking> findByTeacherId(UUID teacherId, Pageable pageable);

    Page<Booking> findByLearnerId(UUID learnerId, Pageable pageable);

    List<Booking> findByStatusAndScheduledAtBefore(BookingStatus status, Instant before);

    List<Booking> findByStatusAndUpdatedAtBefore(BookingStatus status, Instant before);

    List<Booking> findByTeacherIdAndScheduledAtBetween(UUID teacherId, Instant from, Instant to);

    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.scheduledAt BETWEEN :from AND :to")
    List<Booking> findRemindersWindow(@Param("status") BookingStatus status,
                                       @Param("from") Instant from,
                                       @Param("to") Instant to);
}