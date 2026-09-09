package com.skillseed.booking.service;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.domain.CancelReason;
import com.skillseed.booking.dto.BookingPageResponse;
import com.skillseed.booking.dto.BookingResponse;
import com.skillseed.booking.dto.BookingSummaryResponse;
import com.skillseed.booking.dto.CancelBookingRequest;
import com.skillseed.booking.dto.CreateBookingRequest;
import com.skillseed.booking.exception.BookingException;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.notification.EmailTemplateService;
import com.skillseed.notification.domain.NotificationType;
import com.skillseed.notification.service.NotificationService;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.repository.SkillRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserAvailabilityRepository;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.wallet.service.SeedWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Booking lifecycle service (T-M100..T-M108). Wraps the wallet ledger for
 * escrow / release / refund and emits in-app notifications on every state
 * transition. Refund policy lives in {@link #REFUND_FULL_HOURS}: cancelling
 * ≥ that many hours before {@code scheduledAt} refunds 100%, otherwise 50%.
 */
@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    static final long REFUND_FULL_HOURS = 24;
    static final long NO_SHOW_GRACE_MINUTES = 10;
    /** Hard cap on list-endpoint page size; matches S7.13's spec ceiling
     *  and is enforced both via @Max(50) on controllers and clamped here
     *  as a defence-in-depth measure. */
    static final int MAX_PAGE_SIZE = 50;
    static final Duration PENDING_TTL = Duration.ofHours(24);

    /** Mirror of {@link #REFUND_FULL_HOURS} in minutes — used at the cancel
     *  boundary so callers can't accidentally lose resolution by going
     * through whole-hour rounding (FR-M76). */
    static final long REFUND_FULL_MINUTES = 24L * 60L;
    private static final List<Integer> ALLOWED_DURATIONS = List.of(15, 30, 45, 60, 90);
    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(
            BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserAvailabilityRepository userAvailabilityRepository;
    private final SeedWalletService seedWalletService;
    private final NotificationService notificationService;
    private final EmailTemplateService emailTemplateService;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            SkillRepository skillRepository,
            UserAvailabilityRepository userAvailabilityRepository,
            SeedWalletService seedWalletService,
            NotificationService notificationService,
            EmailTemplateService emailTemplateService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userAvailabilityRepository = userAvailabilityRepository;
        this.seedWalletService = seedWalletService;
        this.notificationService = notificationService;
        this.emailTemplateService = emailTemplateService;
    }

    @Transactional
    public BookingResponse create(UUID learnerId, CreateBookingRequest req) {
        if (req.teacherId().equals(learnerId)) {
            throw BookingException.unprocessable("CANNOT_BOOK_SELF",
                    "Teacher and learner must be different users");
        }
        Instant scheduledAt = req.scheduledAt();
        Instant now = Instant.now();
        if (!scheduledAt.isAfter(now)) {
            throw BookingException.badRequest("SCHEDULED_AT_INVALID",
                    "scheduledAt must be in the future");
        }
        if (!ALLOWED_DURATIONS.contains(req.durationMinutes())) {
            throw BookingException.badRequest("DURATION_INVALID",
                    "durationMinutes must be one of " + ALLOWED_DURATIONS);
        }
        User teacher = userRepository.findById(req.teacherId())
                .orElseThrow(() -> BookingException.notFound("TEACHER_NOT_FOUND",
                        "Teacher not found"));
        if (teacher.getDeletedAt() != null) {
            throw BookingException.unprocessable("TEACHER_NOT_AVAILABLE",
                    "Teacher account is not active");
        }
        User learner = userRepository.findById(learnerId)
                .orElseThrow(() -> BookingException.notFound("LEARNER_NOT_FOUND",
                        "Learner not found"));
        Skill skill = skillRepository.findById(req.skillId())
                .orElseThrow(() -> BookingException.notFound("SKILL_NOT_FOUND",
                        "Skill not found"));

        if (hasActiveSlotConflict(teacher.getId(), scheduledAt, req.durationMinutes())) {
            throw BookingException.conflict("SLOT_UNAVAILABLE",
                    "Teacher already has an active booking overlapping this slot");
        }
        if (!isWithinAvailability(teacher.getId(), scheduledAt, req.durationMinutes())) {
            throw BookingException.conflict("SLOT_UNAVAILABLE",
                    "Selected slot is outside the teacher's availability");
        }

        int seedAmount = calculateSeedAmount(req.durationMinutes());
        Booking booking = new Booking(
                UUID.randomUUID(),
                teacher,
                learner,
                skill,
                scheduledAt,
                (short) req.durationMinutes().intValue(),
                seedAmount);
        booking.setNotes(req.notes());
        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);
        booking = bookingRepository.save(booking);

        seedWalletService.escrowDebit(booking);

        Map<String, Object> payload = payloadFor(booking);
        notificationService.publish(teacher, NotificationType.BOOKING_REQUEST, payload);
        notificationService.publish(learner, NotificationType.BOOKING_REQUEST, payload);
        emailTemplateService.sendBookingConfirmationEmail(booking, teacher, "teacher");
        emailTemplateService.sendBookingConfirmationEmail(booking, learner, "learner");
        log.info("Created booking id={} teacher={} learner={} scheduledAt={} seeds={}",
                booking.getId(), teacher.getId(), learner.getId(),
                scheduledAt, seedAmount);
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse accept(UUID bookingId, UUID actorId) {
        Booking booking = loadBooking(bookingId);
        if (!actorId.equals(booking.getTeacher().getId())) {
            throw BookingException.forbidden("NOT_TEACHER",
                    "Only the teacher can accept this booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw BookingException.conflict("INVALID_STATE_TRANSITION",
                    "Cannot accept a booking in state " + booking.getStatus().getDbValue());
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);
        notificationService.publish(booking.getLearner(), NotificationType.BOOKING_ACCEPTED,
                payloadFor(booking));
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse decline(UUID bookingId, UUID actorId, String reason) {
        Booking booking = loadBooking(bookingId);
        if (!actorId.equals(booking.getTeacher().getId())) {
            throw BookingException.forbidden("NOT_TEACHER",
                    "Only the teacher can decline this booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw BookingException.conflict("INVALID_STATE_TRANSITION",
                    "Cannot decline a booking in state " + booking.getStatus().getDbValue());
        }
        booking.setStatus(BookingStatus.DECLINED);
        booking.setCancellationReason(reason == null ? "TEACHER_DECLINED" : reason);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);
        seedWalletService.refundEscrow(booking, 100);
        notificationService.publish(booking.getLearner(), NotificationType.BOOKING_DECLINED,
                payloadFor(booking));
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse cancel(UUID bookingId, UUID actorId, CancelBookingRequest req) {
        Booking booking = loadBooking(bookingId);
        ensureParticipant(booking, actorId);
        BookingStatus current = booking.getStatus();
        if (current != BookingStatus.PENDING && current != BookingStatus.CONFIRMED) {
            throw BookingException.conflict("INVALID_STATE_TRANSITION",
                    "Cannot cancel a booking in state " + current.getDbValue());
        }
        long minutesAhead = Duration.between(Instant.now(), booking.getScheduledAt()).toMinutes();
        int refundPercent = minutesAhead >= REFUND_FULL_MINUTES ? 100 : 50;

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> BookingException.notFound("USER_NOT_FOUND", "User not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(req == null || req.reason() == null
                ? CancelReason.OTHER.getDbValue() : req.reason().getDbValue());
        booking.setCancelledBy(actor);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);

        seedWalletService.refundEscrow(booking, refundPercent);
        User other = actorId.equals(booking.getTeacher().getId())
                ? booking.getLearner() : booking.getTeacher();
        notificationService.publish(other, NotificationType.BOOKING_CANCELLED, payloadFor(booking));
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse start(UUID bookingId, UUID actorId) {
        Booking booking = loadBooking(bookingId);
        ensureParticipant(booking, actorId);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw BookingException.conflict("INVALID_STATE_TRANSITION",
                    "Cannot start a booking in state " + booking.getStatus().getDbValue());
        }
        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);
        User other = actorId.equals(booking.getTeacher().getId())
                ? booking.getLearner() : booking.getTeacher();
        notificationService.publish(other, NotificationType.SESSION_STARTED, payloadFor(booking));
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse complete(UUID bookingId, UUID actorId) {
        // Pessimistic lock to serialise against SessionService.markMeetingEnded
        // (Daily webhook can fire concurrently with this "Mark complete" click).
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BookingException.notFound("BOOKING_NOT_FOUND",
                        "Booking not found"));
        ensureParticipant(booking, actorId);
        if (booking.getStatus() != BookingStatus.IN_PROGRESS
                && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw BookingException.conflict("INVALID_STATE_TRANSITION",
                    "Cannot complete a booking in state " + booking.getStatus().getDbValue());
        }
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);
        seedWalletService.releaseEscrow(booking);
        notificationService.publish(booking.getLearner(), NotificationType.SESSION_COMPLETED,
                payloadFor(booking));
        notificationService.publish(booking.getTeacher(), NotificationType.SESSION_COMPLETED,
                payloadFor(booking));
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getById(UUID bookingId, UUID actorId) {
        Booking booking = loadBooking(bookingId);
        if (!actorId.equals(booking.getTeacher().getId())
                && !actorId.equals(booking.getLearner().getId())) {
            throw BookingException.forbidden("FORBIDDEN",
                    "You are not a participant of this booking");
        }
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingPageResponse listForUser(UUID userId, String role, List<BookingStatus> statuses,
                                            int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? 20 : Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Direction.DESC, "scheduledAt"));
        Page<Booking> result;
        List<BookingStatus> filter = statuses == null || statuses.isEmpty()
                ? null : statuses;
        if ("teacher".equalsIgnoreCase(role)) {
            result = filter == null
                    ? bookingRepository.findByTeacherId(userId, pageable)
                    : bookingRepository.findByTeacherIdAndStatusIn(userId, filter, pageable);
        } else if ("learner".equalsIgnoreCase(role)) {
            result = filter == null
                    ? bookingRepository.findByLearnerId(userId, pageable)
                    : bookingRepository.findByLearnerIdAndStatusIn(userId, filter, pageable);
        } else {
            throw BookingException.badRequest("ROLE_INVALID",
                    "role must be 'teacher' or 'learner'");
        }
        return new BookingPageResponse(
                result.getContent().stream().map(BookingSummaryResponse::from).toList(),
                safePage,
                safeSize,
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Transactional
    public int expirePendingBookings() {
        Instant cutoff = Instant.now().minus(PENDING_TTL);
        List<Booking> stale = bookingRepository.findByStatusAndScheduledAtBefore(
                BookingStatus.PENDING, cutoff);
        int count = 0;
        for (Booking booking : stale) {
            booking.setStatus(BookingStatus.EXPIRED);
            booking.setUpdatedAt(Instant.now());
            bookingRepository.save(booking);
            seedWalletService.forfeitEscrow(booking);
            notificationService.publish(booking.getLearner(),
                    NotificationType.BOOKING_CANCELLED, payloadFor(booking));
            notificationService.publish(booking.getTeacher(),
                    NotificationType.BOOKING_CANCELLED, payloadFor(booking));
            count++;
        }
        if (count > 0) {
            log.info("Expired {} pending bookings", count);
        }
        return count;
    }

    @Transactional
    public int markNoShows() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(NO_SHOW_GRACE_MINUTES));
        List<Booking> confirmed = bookingRepository.findByStatusAndScheduledAtBefore(
                BookingStatus.CONFIRMED, cutoff);
        int count = 0;
        for (Booking booking : confirmed) {
            booking.setStatus(BookingStatus.NO_SHOW);
            booking.setUpdatedAt(Instant.now());
            bookingRepository.save(booking);
            seedWalletService.forfeitEscrow(booking);
            notificationService.publish(booking.getLearner(),
                    NotificationType.BOOKING_CANCELLED, payloadFor(booking));
            count++;
        }
        if (count > 0) {
            log.info("Marked {} bookings as NO_SHOW", count);
        }
        return count;
    }

    @Transactional
    public int sendReminders() {
        Instant now = Instant.now();
        Instant cutoff24hStart = now.plus(Duration.ofHours(23)).plus(Duration.ofMinutes(50));
        Instant cutoff24hEnd = now.plus(Duration.ofHours(24)).plus(Duration.ofMinutes(10));
        Instant cutoff1hStart = now.plus(Duration.ofMinutes(50));
        Instant cutoff1hEnd = now.plus(Duration.ofHours(1)).plus(Duration.ofMinutes(10));
        List<Booking> window24h = bookingRepository.findRemindersWindow(
                BookingStatus.CONFIRMED, cutoff24hStart, cutoff24hEnd);
        List<Booking> window1h = bookingRepository.findRemindersWindow(
                BookingStatus.CONFIRMED, cutoff1hStart, cutoff1hEnd);
        int count = 0;
        for (Booking booking : window24h) {
            Map<String, Object> payload = payloadFor(booking);
            payload.put("window", "24h");
            notificationService.publish(booking.getTeacher(),
                    NotificationType.BOOKING_REMINDER_24H, payload);
            notificationService.publish(booking.getLearner(),
                    NotificationType.BOOKING_REMINDER_24H, payload);
            count++;
        }
        for (Booking booking : window1h) {
            Map<String, Object> payload = payloadFor(booking);
            payload.put("window", "1h");
            notificationService.publish(booking.getTeacher(),
                    NotificationType.BOOKING_REMINDER_1H, payload);
            notificationService.publish(booking.getLearner(),
                    NotificationType.BOOKING_REMINDER_1H, payload);
            count++;
        }
        if (count > 0) {
            log.info("Sent {} reminders", count);
        }
        return count;
    }

    private Booking loadBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> BookingException.notFound("BOOKING_NOT_FOUND",
                        "Booking not found"));
    }

    private boolean hasActiveSlotConflict(UUID teacherId, Instant scheduledAt, int durationMinutes) {
        Duration padding = Duration.ofMinutes(60);
        Instant windowStart = scheduledAt.minus(Duration.ofMinutes(durationMinutes)).minus(padding);
        Instant windowEnd = scheduledAt.plus(Duration.ofMinutes(durationMinutes)).plus(padding);
        List<Booking> nearby = bookingRepository.findByTeacherIdAndScheduledAtBetween(
                teacherId, windowStart, windowEnd);
        Instant end = scheduledAt.plus(Duration.ofMinutes(durationMinutes));
        return nearby.stream()
                .filter(b -> ACTIVE_STATUSES.contains(b.getStatus()))
                .anyMatch(b -> overlaps(b, scheduledAt, end));
    }

    private boolean overlaps(Booking b, Instant start, Instant end) {
        Instant bEnd = b.getScheduledAt().plus(Duration.ofMinutes(b.getDurationMinutes()));
        return b.getScheduledAt().isBefore(end) && start.isBefore(bEnd);
    }

    private boolean isWithinAvailability(UUID teacherId, Instant scheduledAt, int durationMinutes) {
        User teacher = userRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return false;
        }
        ZoneId zone = ZoneId.of(teacher.getTimezone());
        ZonedDateTime start = scheduledAt.atZone(zone);
        ZonedDateTime end = start.plus(Duration.ofMinutes(durationMinutes));
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            return false;
        }
        int dayOfWeek = start.getDayOfWeek().getValue() % 7;
        var slots = userAvailabilityRepository.findByUserIdAndDayOfWeek(teacherId, (short) dayOfWeek);
        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();
        return slots.stream().anyMatch(slot -> {
            Instant slotStart = start.toLocalDate()
                    .atTime(slot.getStartTime())
                    .atZone(zone)
                    .toInstant();
            Instant slotEnd = start.toLocalDate()
                    .atTime(slot.getEndTime())
                    .atZone(zone)
                    .toInstant();
            return !slotStart.isAfter(startInstant) && !slotEnd.isBefore(endInstant);
        });
    }

    private void ensureParticipant(Booking booking, UUID actorId) {
        if (!actorId.equals(booking.getTeacher().getId())
                && !actorId.equals(booking.getLearner().getId())) {
            throw BookingException.forbidden("FORBIDDEN",
                    "You are not a participant of this booking");
        }
    }

    /**
     * Pricing table per screens-svg/04-booking/01-booking-modal.svg:62-80.
     * 15/30 min cost 1 seed (introductory), 45 min = 2, 60 min = 3, 90 min = 4.
     */
    static int calculateSeedAmount(int durationMinutes) {
        return switch (durationMinutes) {
            case 15, 30 -> 1;
            case 45 -> 2;
            case 60 -> 3;
            case 90 -> 4;
            default -> throw com.skillseed.booking.exception.BookingException
                    .badRequest("INVALID_DURATION",
                        "Duration must be one of 15, 30, 45, 60, or 90 minutes");
        };
    }

    private Map<String, Object> payloadFor(Booking booking) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("bookingId", booking.getId().toString());
        map.put("scheduledAt", booking.getScheduledAt().toString());
        map.put("durationMinutes", booking.getDurationMinutes());
        map.put("seedAmount", booking.getSeedAmount());
        map.put("status", booking.getStatus().getDbValue());
        return map;
    }
}
