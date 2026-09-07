package com.skillseed.session.service;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.session.client.DailyClient;
import com.skillseed.session.client.DailyDtos;
import com.skillseed.session.dto.SessionRoomResponse;
import com.skillseed.session.exception.SessionException;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Session orchestration (T-M151). Wraps Daily.co room lifecycle behind
 * a booking-scoped API:
 *
 * <ul>
 *   <li>Idempotent — calling {@link #createRoom} twice for the same
 *       booking reuses the stored {@code meeting_url} instead of
 *       spawning multiple rooms.</li>
 *   <li>Authoritative — only {@code teacher} and {@code learner} on
 *       the booking can mint a token for that room.</li>
 *   <li>Bounded — the room expiry is {@code scheduledAt + duration + grace},
 *       matching the design §7.3 contract.</li>
 * </ul>
 */
@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final DailyClient dailyClient;
    private final long graceMinutes;

    public SessionService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            DailyClient dailyClient,
            @Value("${session.daily.grace-minutes:30}") long graceMinutes) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.dailyClient = dailyClient;
        this.graceMinutes = graceMinutes;
    }

    @Transactional
    public SessionRoomResponse createRoom(UUID bookingId, UUID actorId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> SessionException.notFound("BOOKING_NOT_FOUND",
                        "Booking not found"));

        boolean isTeacher = actorId.equals(booking.getTeacher().getId());
        boolean isLearner = actorId.equals(booking.getLearner().getId());
        if (!isTeacher && !isLearner) {
            throw SessionException.forbidden("NOT_PARTICIPANT",
                    "Only the teacher or learner can join this session");
        }

        BookingStatus status = booking.getStatus();
        if (status != BookingStatus.CONFIRMED
                && status != BookingStatus.IN_PROGRESS) {
            throw SessionException.conflict("INVALID_STATE_TRANSITION",
                    "Cannot join a session in state " + status.getDbValue());
        }

        User participant = userRepository.findById(actorId)
                .orElseThrow(() -> SessionException.notFound("USER_NOT_FOUND",
                        "User not found"));

        Instant scheduledAt = booking.getScheduledAt();
        int durationMinutes = booking.getDurationMinutes();
        Instant expiresAt = scheduledAt
                .plus(Duration.ofMinutes(durationMinutes))
                .plus(Duration.ofMinutes(graceMinutes));

        String roomUrl = booking.getMeetingUrl();
        String roomName = extractRoomName(roomUrl);
        if (roomName == null) {
            roomName = deriveRoomName(bookingId);
            if (!dailyClient.isConfigured()) {
                throw SessionException.unprocessable("DAILY_DISABLED",
                        "Daily.co integration is not configured");
            }
            DailyDtos.RoomResponse room = dailyClient.createRoom(roomName, expiresAt);
            roomName = room.getName();
            roomUrl = room.getUrl();
            booking.setMeetingUrl(roomUrl);
            booking.setUpdatedAt(Instant.now());
            bookingRepository.save(booking);
            log.info("Created Daily room {} for booking {}", roomName, bookingId);
        } else {
            log.debug("Reusing Daily room {} for booking {}", roomName, bookingId);
        }

        String token = dailyClient.createMeetingToken(
                participant.getFullName() == null ? participant.getEmail() : participant.getFullName(),
                actorId.toString(),
                isTeacher,
                expiresAt);

        return new SessionRoomResponse(
                bookingId,
                roomUrl,
                roomName,
                token,
                isTeacher ? "owner" : "guest",
                expiresAt,
                scheduledAt,
                durationMinutes);
    }

    /**
     * Mark a booking complete from the Daily.co webhook. Idempotent: if
     * the booking is already completed / cancelled / no-show we silently
     * skip so Daily's at-least-once delivery doesn't double-credit the
     * wallet.
     */
    @Transactional
    public void markMeetingEnded(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.warn("meeting.ended webhook for unknown booking {}", bookingId);
            return;
        }
        BookingStatus status = booking.getStatus();
        if (status == BookingStatus.COMPLETED
                || status == BookingStatus.RATED
                || status == BookingStatus.CANCELLED
                || status == BookingStatus.DECLINED
                || status == BookingStatus.EXPIRED
                || status == BookingStatus.NO_SHOW) {
            log.debug("Skipping meeting.ended for booking {} in terminal state {}", bookingId, status);
            return;
        }
        if (status != BookingStatus.IN_PROGRESS && status != BookingStatus.CONFIRMED) {
            log.warn("Refusing meeting.ended for booking {} in state {}", bookingId, status);
            return;
        }
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);
        log.info("Marked booking {} complete via Daily webhook", bookingId);
    }

    static String deriveRoomName(UUID bookingId) {
        String compact = bookingId.toString().replace("-", "");
        return "ss-" + compact.substring(0, Math.min(compact.length(), 24));
    }

    static String extractRoomName(String roomUrl) {
        if (roomUrl == null || roomUrl.isBlank()) {
            return null;
        }
        int slash = roomUrl.lastIndexOf('/');
        if (slash < 0 || slash == roomUrl.length() - 1) {
            return null;
        }
        return roomUrl.substring(slash + 1);
    }
}
