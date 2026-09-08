package com.skillseed.booking.repository;

import com.skillseed.booking.domain.Booking;
import com.skillseed.shared.domain.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Page<Booking> findByTeacherIdAndStatusIn(UUID teacherId, List<BookingStatus> statuses, Pageable pageable);

    Page<Booking> findByLearnerIdAndStatusIn(UUID learnerId, List<BookingStatus> statuses, Pageable pageable);

    Page<Booking> findByTeacherId(UUID teacherId, Pageable pageable);

    Page<Booking> findByLearnerId(UUID learnerId, Pageable pageable);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByStatusAndScheduledAtBefore(BookingStatus status, Instant before);

    List<Booking> findByStatusAndUpdatedAtBefore(BookingStatus status, Instant before);

    List<Booking> findByTeacherIdAndScheduledAtBetween(UUID teacherId, Instant from, Instant to);

    List<Booking> findByTeacherId(UUID teacherId);

    List<Booking> findByLearnerId(UUID learnerId);

    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.scheduledAt BETWEEN :from AND :to")
    List<Booking> findRemindersWindow(@Param("status") BookingStatus status,
                                       @Param("from") Instant from,
                                       @Param("to") Instant to);

    /**
     * Pessimistic-write lock variant of findById. Use this when the
     * caller intends to mutate status / completed_at to defend
     * against the TOCTOU race between BookingService.complete (user
     * "Mark complete" button) and SessionService.markMeetingEnded
     * (Daily webhook arrival). Hibernate issues SELECT ... FOR UPDATE
     * which serializes the two transactions; the loser waits, then
     * re-reads the now-terminal state and bails out gracefully.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") UUID id);
}