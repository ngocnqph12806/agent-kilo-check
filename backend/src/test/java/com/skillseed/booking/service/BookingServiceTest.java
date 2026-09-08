package com.skillseed.booking.service;

import com.skillseed.booking.domain.CancelReason;
import com.skillseed.booking.dto.BookingResponse;
import com.skillseed.booking.dto.CancelBookingRequest;
import com.skillseed.booking.dto.CreateBookingRequest;
import com.skillseed.booking.exception.BookingException;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.notification.EmailTemplateService;
import com.skillseed.notification.service.NotificationService;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.repository.SkillRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserAvailability;
import com.skillseed.user.repository.UserAvailabilityRepository;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.wallet.exception.WalletException;
import com.skillseed.wallet.service.SeedWalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BookingService}. Covers the happy path + state
 * transitions + refund policy without booting the Spring context (T-M140).
 *
 * <p>The wallet, repository, notification and skill collaborators are all
 * mocked so the service can be exercised in isolation. Slot-conflict +
 * availability logic is bypassed by stubbing the repository to return no
 * nearby bookings and one matching availability row.
 */
class BookingServiceTest {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private SkillRepository skillRepository;
    private UserAvailabilityRepository availabilityRepository;
    private SeedWalletService walletService;
    private NotificationService notificationService;
    private EmailTemplateService emailTemplateService;
    private BookingService service;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        skillRepository = mock(SkillRepository.class);
        availabilityRepository = mock(UserAvailabilityRepository.class);
        walletService = mock(SeedWalletService.class);
        notificationService = mock(NotificationService.class);
        emailTemplateService = mock(EmailTemplateService.class);
        service = new BookingService(
                bookingRepository,
                userRepository,
                skillRepository,
                availabilityRepository,
                walletService,
                notificationService,
                emailTemplateService);
    }

    @Test
    void createEscrowsAndPublishesBookingRequest() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();
        Instant scheduledAt = Instant.now().plus(48, ChronoUnit.HOURS);
        User teacher = userWithTimezone(teacherId, "UTC");
        User learner = new User();
        setField(learner, "id", learnerId);
        Skill skill = skillWithId(skillId);

        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(userRepository.findById(learnerId)).thenReturn(Optional.of(learner));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(bookingRepository.findByTeacherIdAndScheduledAtBetween(eq(teacherId), any(), any()))
                .thenReturn(List.of());
        when(availabilityRepository.findByUserIdAndDayOfWeek(eq(teacherId), any(Short.class)))
                .thenReturn(List.of(slotForTeacherTimezone(teacher, scheduledAt, 60)));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreateBookingRequest req = new CreateBookingRequest(
                teacherId, skillId, scheduledAt, 60, "Want to learn basics");

        BookingResponse resp = service.create(learnerId, req);

        assertThat(resp.status()).isEqualTo("pending");
        assertThat(resp.seedAmount()).isEqualTo(60);
        assertThat(resp.notes()).isEqualTo("Want to learn basics");
        verify(walletService).escrowDebit(any());
        verify(notificationService, times(2)).publish(any(), any(), any());
        verify(emailTemplateService, times(2)).sendBookingConfirmationEmail(any(), any(), any());
    }

    @Test
    void createRejectsSelfBooking() {
        UUID learnerId = UUID.randomUUID();
        CreateBookingRequest req = new CreateBookingRequest(
                learnerId, UUID.randomUUID(), Instant.now().plusSeconds(3600),
                30, null);

        assertThatThrownBy(() -> service.create(learnerId, req))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("Teacher and learner");
        verify(walletService, never()).escrowDebit(any());
    }

    @Test
    void createRejectsPastSchedule() {
        CreateBookingRequest req = new CreateBookingRequest(
                UUID.randomUUID(), UUID.randomUUID(),
                Instant.now().minusSeconds(60), 30, null);

        assertThatThrownBy(() -> service.create(UUID.randomUUID(), req))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("future");
    }

    @Test
    void createRejectsInvalidDuration() {
        CreateBookingRequest req = new CreateBookingRequest(
                UUID.randomUUID(), UUID.randomUUID(),
                Instant.now().plusSeconds(3600), 20, null);

        assertThatThrownBy(() -> service.create(UUID.randomUUID(), req))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("durationMinutes");
    }

    @Test
    void createSurfacesInsufficientBalanceFromWallet() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Instant scheduledAt = Instant.now().plus(48, ChronoUnit.HOURS);
        User teacher = userWithTimezone(teacherId, "UTC");
        User learner = new User();
        setField(learner, "id", learnerId);
        Skill skill = skillWithId(UUID.randomUUID());

        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(userRepository.findById(learnerId)).thenReturn(Optional.of(learner));
        when(skillRepository.findById(any())).thenReturn(Optional.of(skill));
        when(bookingRepository.findByTeacherIdAndScheduledAtBetween(eq(teacherId), any(), any()))
                .thenReturn(List.of());
        when(availabilityRepository.findByUserIdAndDayOfWeek(eq(teacherId), any(Short.class)))
                .thenReturn(List.of(slotForTeacherTimezone(teacher, scheduledAt, 60)));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(WalletException.conflict("INSUFFICIENT_BALANCE", "no funds"))
                .when(walletService).escrowDebit(any());

        CreateBookingRequest req = new CreateBookingRequest(
                teacherId, skill.getId(), scheduledAt, 60, null);

        assertThatThrownBy(() -> service.create(learnerId, req))
                .isInstanceOf(WalletException.class)
                .satisfies(ex -> assertThat(((WalletException) ex).getCode())
                        .isEqualTo("INSUFFICIENT_BALANCE"));
    }

    @Test
    void acceptOnlyAllowsTeacherAndConfirmsState() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        BookingStub booking = bookingFixture(teacherId, learnerId, BookingStatus.PENDING);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));

        BookingResponse resp = service.accept(booking.id, teacherId);

        assertThat(resp.status()).isEqualTo("confirmed");
        ArgumentCaptor<com.skillseed.booking.domain.Booking> captor =
                ArgumentCaptor.forClass(com.skillseed.booking.domain.Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(notificationService).publish(eq(booking.learner), any(), any());
    }

    @Test
    void acceptRejectsLearner() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        BookingStub booking = bookingFixture(teacherId, learnerId, BookingStatus.PENDING);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));

        assertThatThrownBy(() -> service.accept(booking.id, learnerId))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("teacher");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void acceptRejectsNonPendingState() {
        UUID teacherId = UUID.randomUUID();
        BookingStub booking = bookingFixture(teacherId, UUID.randomUUID(), BookingStatus.CONFIRMED);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));

        assertThatThrownBy(() -> service.accept(booking.id, teacherId))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("state");
    }

    @Test
    void declineRefundsFullAndMarksDeclined() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        BookingStub booking = bookingFixture(teacherId, learnerId, BookingStatus.PENDING);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));

        BookingResponse resp = service.decline(booking.id, teacherId, "busy");

        assertThat(resp.status()).isEqualTo("declined");
        assertThat(resp.cancellationReason()).isEqualTo("busy");
        verify(walletService).refundEscrow(any(), eq(100));
        verify(notificationService).publish(eq(booking.learner), any(), any());
    }

    @Test
    void cancelByTeacher24hAheadRefundsFull() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Instant scheduledAt = Instant.now().plus(48, ChronoUnit.HOURS);
        BookingStub booking = bookingFixture(teacherId, learnerId,
                BookingStatus.CONFIRMED, scheduledAt);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(booking.teacher));

        service.cancel(booking.id, teacherId,
                new CancelBookingRequest(CancelReason.TEACHER_UNAVAILABLE, null));

        verify(walletService).refundEscrow(any(), eq(100));
    }

    @Test
    void cancelInside24hRefundsHalf() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Instant scheduledAt = Instant.now().plus(6, ChronoUnit.HOURS);
        BookingStub booking = bookingFixture(teacherId, learnerId,
                BookingStatus.CONFIRMED, scheduledAt);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));
        when(userRepository.findById(learnerId)).thenReturn(Optional.of(booking.learner));

        service.cancel(booking.id, learnerId,
                new CancelBookingRequest(CancelReason.LEARNER_UNAVAILABLE, "sorry"));

        verify(walletService).refundEscrow(any(), eq(50));
    }

    @Test
    void cancelAtExactly24hBoundaryRefundsFull() {
        // FR-M76: the 24h boundary must be evaluated in MINUTES (not whole
        // hours) so a cancel 24h00m ahead still earns a full refund.
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        // 24 * 60 + 1 minutes so the test never reads false-positive due to
        // clock drift between now() and the service's internal Instant.now().
        Instant scheduledAt = Instant.now().plus(24 * 60 + 1, ChronoUnit.MINUTES);
        BookingStub booking = bookingFixture(teacherId, learnerId,
                BookingStatus.CONFIRMED, scheduledAt);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(booking.teacher));

        service.cancel(booking.id, teacherId,
                new CancelBookingRequest(CancelReason.TEACHER_UNAVAILABLE, null));

        verify(walletService).refundEscrow(any(), eq(100));
    }

    @Test
    void cancelOneMinuteUnder24hBoundaryRefundsHalf() {
        // 23h59m ahead is strictly less than the FR-M76 threshold, so a
        // half-refund (50%) is the correct policy.
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Instant scheduledAt = Instant.now().plus(23 * 60 + 59, ChronoUnit.MINUTES);
        BookingStub booking = bookingFixture(teacherId, learnerId,
                BookingStatus.CONFIRMED, scheduledAt);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(booking.teacher));

        service.cancel(booking.id, teacherId,
                new CancelBookingRequest(CancelReason.TEACHER_UNAVAILABLE, null));

        verify(walletService).refundEscrow(any(), eq(50));
    }

    @Test
    void cancelRejectsNonParticipant() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        UUID outsider = UUID.randomUUID();
        BookingStub booking = bookingFixture(teacherId, learnerId, BookingStatus.CONFIRMED);
        when(bookingRepository.findById(booking.id)).thenReturn(Optional.of(booking.entity));

        assertThatThrownBy(() -> service.cancel(booking.id, outsider,
                new CancelBookingRequest(CancelReason.OTHER, null)))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("participant");
    }

    @Test
    void completeReleasesEscrowAndNotifiesBothParties() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        BookingStub booking = bookingFixture(teacherId, learnerId, BookingStatus.IN_PROGRESS);
        when(bookingRepository.findByIdForUpdate(booking.id)).thenReturn(Optional.of(booking.entity));

        BookingResponse resp = service.complete(booking.id, teacherId);

        assertThat(resp.status()).isEqualTo("completed");
        verify(walletService).releaseEscrow(any());
        verify(notificationService, times(2)).publish(any(), any(), any());
    }

    @Test
    void expirePendingBookingsForfeitsEscrowAndNotifies() {
        Instant scheduledAt = Instant.now().minus(48, ChronoUnit.HOURS);
        BookingStub booking = bookingFixture(UUID.randomUUID(), UUID.randomUUID(),
                BookingStatus.PENDING, scheduledAt);
        when(bookingRepository.findByStatusAndScheduledAtBefore(eq(BookingStatus.PENDING), any()))
                .thenReturn(List.of(booking.entity));

        int count = service.expirePendingBookings();

        assertThat(count).isEqualTo(1);
        ArgumentCaptor<com.skillseed.booking.domain.Booking> captor =
                ArgumentCaptor.forClass(com.skillseed.booking.domain.Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(BookingStatus.EXPIRED);
        verify(walletService).forfeitEscrow(any());
        verify(notificationService, atLeastOnce()).publish(any(), any(), any());
    }

    @Test
    void markNoShowsForfeitsEscrowAfterGracePeriod() {
        Instant scheduledAt = Instant.now().minus(20, ChronoUnit.MINUTES);
        BookingStub booking = bookingFixture(UUID.randomUUID(), UUID.randomUUID(),
                BookingStatus.CONFIRMED, scheduledAt);
        when(bookingRepository.findByStatusAndScheduledAtBefore(eq(BookingStatus.CONFIRMED), any()))
                .thenReturn(List.of(booking.entity));

        int count = service.markNoShows();

        assertThat(count).isEqualTo(1);
        ArgumentCaptor<com.skillseed.booking.domain.Booking> captor =
                ArgumentCaptor.forClass(com.skillseed.booking.domain.Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(BookingStatus.NO_SHOW);
        verify(walletService).forfeitEscrow(any());
    }

    private User userWithTimezone(UUID id, String tz) {
        User user = new User();
        setField(user, "id", id);
        setField(user, "timezone", tz);
        return user;
    }

    private Skill skillWithId(UUID id) {
        Skill skill = new Skill(id, "java-basics", "Java Basics",
                com.skillseed.shared.domain.SkillCategory.TECH);
        return skill;
    }

    private UserAvailability slotForTeacherTimezone(User teacher, Instant scheduledAt, int minutes) {
        ZoneId zone = ZoneId.of(teacher.getTimezone());
        ZonedDateTime start = scheduledAt.atZone(zone);
        ZonedDateTime end = start.plus(minutes, ChronoUnit.MINUTES);
        short dayOfWeek = (short) (start.getDayOfWeek().getValue() % 7);
        return new UserAvailability(
                UUID.randomUUID(),
                teacher,
                dayOfWeek,
                start.toLocalTime().minusHours(1),
                end.toLocalTime().plusHours(1),
                teacher.getTimezone());
    }

    private BookingStub bookingFixture(UUID teacherId, UUID learnerId, BookingStatus status) {
        return bookingFixture(teacherId, learnerId, status,
                Instant.now().plus(48, ChronoUnit.HOURS));
    }

    private BookingStub bookingFixture(UUID teacherId, UUID learnerId,
                                        BookingStatus status, Instant scheduledAt) {
        User teacher = mock(User.class);
        when(teacher.getId()).thenReturn(teacherId);
        when(teacher.getFullName()).thenReturn("Teacher");
        User learner = mock(User.class);
        when(learner.getId()).thenReturn(learnerId);
        when(learner.getFullName()).thenReturn("Learner");
        Skill skill = mock(Skill.class);
        when(skill.getId()).thenReturn(UUID.randomUUID());
        when(skill.getName()).thenReturn("Java");
        when(skill.getSlug()).thenReturn("java");
        when(skill.getCategory()).thenReturn(com.skillseed.shared.domain.SkillCategory.TECH);

        com.skillseed.booking.domain.Booking booking =
                new com.skillseed.booking.domain.Booking(
                        UUID.randomUUID(), teacher, learner, skill,
                        scheduledAt, (short) 30, 30);
        booking.setStatus(status);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());
        return new BookingStub(booking.getId(), booking, teacher, learner);
    }

    private static void setField(Object target, String name, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private record BookingStub(
            UUID id,
            com.skillseed.booking.domain.Booking entity,
            User teacher,
            User learner) {
    }
}