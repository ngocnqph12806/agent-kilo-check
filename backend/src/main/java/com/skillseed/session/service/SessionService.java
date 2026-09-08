package com.skillseed.session.service;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.booking.service.BookingService;
import com.skillseed.session.client.DailyClient;
import com.skillseed.session.client.DailyDtos;
import com.skillseed.session.client.DailyProperties;
import com.skillseed.session.domain.SessionIncident;
import com.skillseed.session.dto.ReportIssueRequest;
import com.skillseed.session.dto.ReportIssueResponse;
import com.skillseed.session.dto.SessionRoomResponse;
import com.skillseed.session.exception.SessionException;
import com.skillseed.session.repository.SessionIncidentRepository;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Application service for video sessions. Bridges the booking
 * lifecycle with the Daily.co REST API: callers (a REST controller
 * or the webhook handler) ask for a room to be created/reused for
 * a given booking; this service enforces participant authorisation
 * and state-machine rules, then mints a per-participant meeting token.
 *
 * <p>Room naming follows the {@code ss-<bookingIdCompact>} scheme so
 * that incoming {@code meeting.ended} webhooks can be routed back to
 * the originating booking without needing a separate lookup table.
 */
@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    static final String ROOM_NAME_PREFIX = "ss-";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final DailyClient dailyClient;
    private final DailyProperties dailyProperties;
    private final SessionIncidentRepository incidentRepository;

    public SessionService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          BookingService bookingService,
                          DailyClient dailyClient,
                          DailyProperties dailyProperties,
                          SessionIncidentRepository incidentRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.dailyClient = dailyClient;
        this.dailyProperties = dailyProperties;
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public SessionRoomResponse createRoom(UUID bookingId, UUID actorId) {
        if (!dailyProperties.isEnabled()) {
            log.debug("Daily integration disabled — issuing stub room for booking {}", bookingId);
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> SessionException.notFound("BOOKING_NOT_FOUND",
                        "Booking " + bookingId + " not found"));

        if (!actorId.equals(booking.getTeacher().getId())
                && !actorId.equals(booking.getLearner().getId())) {
            throw SessionException.forbidden("NOT_PARTICIPANT",
                    "You are not a participant of this booking");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED
                && booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw SessionException.unprocessable("BOOKING_NOT_READY",
                    "Booking is in state " + booking.getStatus().getDbValue()
                            + "; can only open a room for confirmed or in-progress bookings");
        }

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> SessionException.notFound("USER_NOT_FOUND",
                        "User " + actorId + " not found"));

        String roomName = deriveRoomName(bookingId);
        Instant scheduledAt = booking.getScheduledAt();
        int duration = booking.getDurationMinutes();
        long graceSeconds = dailyProperties.getGraceMinutes() * 60L;
        long expSeconds = scheduledAt
                .plusSeconds(duration * 60L)
                .plusSeconds(graceSeconds)
                .getEpochSecond();

        DailyDtos.CreateRoomResponse room = dailyProperties.isEnabled()
                ? dailyClient.createRoom(roomName, expSeconds)
                : stubRoom(roomName, expSeconds);
        boolean isOwner = actorId.equals(booking.getTeacher().getId());
        String token = dailyProperties.isEnabled()
                ? dailyClient.createMeetingToken(
                        roomName,
                        actorId.toString(),
                        actor.getFullName(),
                        isOwner,
                        expSeconds)
                : stubToken(actorId);

        return new SessionRoomResponse(
                room.getUrl(),
                room.getName(),
                token,
                isOwner ? "owner" : "participant",
                Instant.ofEpochSecond(expSeconds),
                scheduledAt,
                duration);
    }

    private static DailyDtos.CreateRoomResponse stubRoom(String roomName, long expSeconds) {
        DailyDtos.CreateRoomResponse stub = new DailyDtos.CreateRoomResponse();
        stub.setName(roomName);
        stub.setUrl("https://stub.daily.co/" + roomName);
        return stub;
    }

    private static String stubToken(UUID actorId) {
        return "stub-token-" + actorId.toString().replace("-", "");
    }

    /**
     * React to a {@code meeting.ended} webhook from Daily.co. Looks up
     * the booking whose room name encodes its id and flips it to
     * {@link BookingStatus#COMPLETED}. Idempotent: bookings already in
     * a terminal state are left alone.
     */
    @Transactional
    public void markMeetingEnded(String roomName) {
        UUID bookingId = extractBookingId(roomName);
        if (bookingId == null) {
            log.warn("Ignoring meeting.ended for unparseable room name '{}'", roomName);
            return;
        }
        // Pessimistic lock to serialise against BookingService.complete
        // (user clicks "Mark complete" at almost the same moment Daily
        // posts meeting.ended). Whichever transaction commits first wins;
        // the loser observes the terminal state and exits gracefully.
        Booking booking = bookingRepository.findByIdForUpdate(bookingId).orElse(null);
        if (booking == null) {
            log.warn("Ignoring meeting.ended for unknown booking {}", bookingId);
            return;
        }
        BookingStatus status = booking.getStatus();
        if (status == BookingStatus.COMPLETED
                || status == BookingStatus.CANCELLED
                || status == BookingStatus.DECLINED
                || status == BookingStatus.EXPIRED
                || status == BookingStatus.NO_SHOW
                || status == BookingStatus.RATED) {
            log.info("Booking {} already in terminal state {}; ignoring meeting.ended",
                    bookingId, status);
            return;
        }
        if (status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED
                || status == BookingStatus.IN_PROGRESS) {
            bookingService.complete(bookingId, booking.getLearner().getId());
        }
    }

    public static String deriveRoomName(UUID bookingId) {
        return ROOM_NAME_PREFIX + bookingId.toString().replace("-", "");
    }

    /**
     * FR-M57: open an incident ticket for an in-session problem. The
     * caller must be a participant on the booking. Returns the
     * newly-created incident so the FE can show its id back to the
     * reporter and the ops dashboard can pick it up.
     */
    @Transactional
    public ReportIssueResponse reportIssue(UUID bookingId, UUID reporterId, ReportIssueRequest req) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> SessionException.notFound("BOOKING_NOT_FOUND",
                        "Booking " + bookingId + " not found"));
        if (!reporterId.equals(booking.getTeacher().getId())
                && !reporterId.equals(booking.getLearner().getId())) {
            throw SessionException.forbidden("NOT_PARTICIPANT",
                    "You are not a participant of this booking");
        }
        SessionIncident incident = new SessionIncident(
                UUID.randomUUID(),
                bookingId,
                reporterId,
                req.getCategory(),
                req.getDescription(),
                Instant.now());
        incidentRepository.save(incident);
        return new ReportIssueResponse(
                incident.getId(),
                bookingId,
                incident.getStatus(),
                incident.getCreatedAt());
    }

    public static UUID extractBookingId(String roomName) {
        if (roomName == null || !roomName.startsWith(ROOM_NAME_PREFIX)) {
            return null;
        }
        String compact = roomName.substring(ROOM_NAME_PREFIX.length());
        if (compact.length() != 32) {
            return null;
        }
        try {
            String dashed = compact.substring(0, 8) + "-"
                    + compact.substring(8, 12) + "-"
                    + compact.substring(12, 16) + "-"
                    + compact.substring(16, 20) + "-"
                    + compact.substring(20, 32);
            return UUID.fromString(dashed);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
