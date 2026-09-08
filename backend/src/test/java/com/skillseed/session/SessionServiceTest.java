package com.skillseed.session;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.booking.service.BookingService;
import com.skillseed.session.client.DailyClient;
import com.skillseed.session.client.DailyDtos;
import com.skillseed.session.client.DailyProperties;
import com.skillseed.session.dto.SessionRoomResponse;
import com.skillseed.session.exception.SessionException;
import com.skillseed.session.repository.SessionIncidentRepository;
import com.skillseed.session.service.SessionService;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionServiceTest {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private BookingService bookingService;
    private DailyClient dailyClient;
    private DailyProperties dailyProperties;
    private SessionIncidentRepository incidentRepository;
    private SessionService service;

    private static final UUID BOOKING_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID TEACHER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID LEARNER_ID = UUID.fromString("99999999-8888-7777-6666-555555555555");
    private static final UUID BYSTANDER_ID = UUID.fromString("deadbeef-1111-2222-3333-444444444444");

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        bookingService = mock(BookingService.class);
        dailyClient = mock(DailyClient.class);
        incidentRepository = mock(SessionIncidentRepository.class);
        dailyProperties = new DailyProperties();
        dailyProperties.setEnabled(true);
        dailyProperties.setApiKey("test-key");
        dailyProperties.setGraceMinutes(15);

        service = new SessionService(bookingRepository, userRepository, bookingService,
                dailyClient, dailyProperties, incidentRepository);
    }

    @Test
    void deriveRoomNameUsesCompactUuid() {
        String name = SessionService.deriveRoomName(BOOKING_ID);
        assertThat(name).isEqualTo("ss-" + BOOKING_ID.toString().replace("-", ""));
        assertThat(name).hasSize("ss-".length() + 32);
    }

    @Test
    void extractBookingIdRoundTripsDeriveRoomName() {
        String name = SessionService.deriveRoomName(BOOKING_ID);
        UUID parsed = SessionService.extractBookingId(name);
        assertThat(parsed).isEqualTo(BOOKING_ID);
    }

    @Test
    void extractBookingIdRejectsUnknownPrefix() {
        assertThat(SessionService.extractBookingId("not-our-room")).isNull();
        assertThat(SessionService.extractBookingId(null)).isNull();
        assertThat(SessionService.extractBookingId("ss-short")).isNull();
    }

    @Test
    void createRoomRejectsNonParticipant() {
        Booking booking = bookingFixture(BookingStatus.CONFIRMED, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.createRoom(BOOKING_ID, BYSTANDER_ID))
                .isInstanceOf(SessionException.class)
                .satisfies(ex -> assertThat(((SessionException) ex).getCode())
                        .isEqualTo("NOT_PARTICIPANT"));
        verify(dailyClient, never()).createRoom(anyString(), anyLong());
    }

    @Test
    void createRoomRejectsPendingBooking() {
        Booking booking = bookingFixture(BookingStatus.PENDING, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.createRoom(BOOKING_ID, LEARNER_ID))
                .isInstanceOf(SessionException.class)
                .satisfies(ex -> assertThat(((SessionException) ex).getCode())
                        .isEqualTo("BOOKING_NOT_READY"));
    }

    @Test
    void createRoomMintsRoomAndTokenForConfirmed() {
        Booking booking = bookingFixture(BookingStatus.CONFIRMED, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        User learnerMock = mock(User.class);
        when(learnerMock.getId()).thenReturn(LEARNER_ID);
        when(learnerMock.getFullName()).thenReturn("Aria");
        when(userRepository.findById(LEARNER_ID)).thenReturn(Optional.of(learnerMock));

        DailyDtos.CreateRoomResponse roomResp = new DailyDtos.CreateRoomResponse();
        roomResp.setName(SessionService.deriveRoomName(BOOKING_ID));
        roomResp.setUrl("https://api.daily.co/" + roomResp.getName());
        when(dailyClient.createRoom(anyString(), anyLong())).thenReturn(roomResp);
        when(dailyClient.createMeetingToken(anyString(), anyString(), anyString(),
                anyBoolean(), anyLong())).thenReturn("token-123");

        SessionRoomResponse response = service.createRoom(BOOKING_ID, LEARNER_ID);

        assertThat(response.getRoomUrl()).isEqualTo(roomResp.getUrl());
        assertThat(response.getRoomName()).isEqualTo(roomResp.getName());
        assertThat(response.getToken()).isEqualTo("token-123");
        assertThat(response.getRole()).isEqualTo("participant");
        assertThat(response.getExpiresAt()).isAfter(response.getScheduledAt());

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(dailyClient).createMeetingToken(eq(roomResp.getName()),
                userIdCaptor.capture(), eq("Aria"), eq(false), anyLong());
        assertThat(userIdCaptor.getValue()).isEqualTo(LEARNER_ID.toString());
    }

    @Test
    void createRoomMarksTeacherAsOwner() {
        Booking booking = bookingFixture(BookingStatus.IN_PROGRESS, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        User teacherMock = mock(User.class);
        when(teacherMock.getId()).thenReturn(TEACHER_ID);
        when(teacherMock.getFullName()).thenReturn("Tom");
        when(userRepository.findById(TEACHER_ID)).thenReturn(Optional.of(teacherMock));

        DailyDtos.CreateRoomResponse roomResp = new DailyDtos.CreateRoomResponse();
        roomResp.setName(SessionService.deriveRoomName(BOOKING_ID));
        roomResp.setUrl("https://api.daily.co/" + roomResp.getName());
        when(dailyClient.createRoom(anyString(), anyLong())).thenReturn(roomResp);
        when(dailyClient.createMeetingToken(anyString(), anyString(), anyString(),
                anyBoolean(), anyLong())).thenReturn("teacher-token");

        SessionRoomResponse response = service.createRoom(BOOKING_ID, TEACHER_ID);

        assertThat(response.getRole()).isEqualTo("owner");
        verify(dailyClient).createMeetingToken(anyString(), anyString(), anyString(),
                eq(true), anyLong());
    }

    @Test
    void createRoomWorksWhenDailyDisabled() {
        dailyProperties.setEnabled(false);
        Booking booking = bookingFixture(BookingStatus.CONFIRMED, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        User learnerMock = mock(User.class);
        when(learnerMock.getId()).thenReturn(LEARNER_ID);
        when(learnerMock.getFullName()).thenReturn("Aria");
        when(userRepository.findById(LEARNER_ID)).thenReturn(Optional.of(learnerMock));

        SessionRoomResponse response = service.createRoom(BOOKING_ID, LEARNER_ID);
        assertThat(response.getRoomUrl()).startsWith("https://stub.daily.co/");
        assertThat(response.getToken()).startsWith("stub-token-");
        verify(dailyClient, never()).createRoom(anyString(), anyLong());
    }

    @Test
    void markMeetingEndedCompletesInProgressBooking() {
        Booking booking = bookingFixture(BookingStatus.IN_PROGRESS, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findByIdForUpdate(BOOKING_ID)).thenReturn(Optional.of(booking));

        service.markMeetingEnded(SessionService.deriveRoomName(BOOKING_ID));

        verify(bookingService, times(1)).complete(BOOKING_ID, LEARNER_ID);
    }

    @Test
    void markMeetingEndedIsNoopForTerminalBooking() {
        Booking booking = bookingFixture(BookingStatus.COMPLETED, TEACHER_ID, LEARNER_ID);
        when(bookingRepository.findByIdForUpdate(BOOKING_ID)).thenReturn(Optional.of(booking));

        service.markMeetingEnded(SessionService.deriveRoomName(BOOKING_ID));

        verify(bookingService, never()).complete(any(), any());
    }

    @Test
    void markMeetingEndedIgnoresUnknownRoom() {
        // Prefix matches but the compact body is too short to be a UUID —
        // the service should short-circuit before hitting the repository.
        service.markMeetingEnded("ss-deadbeef");
        verify(bookingRepository, never()).findById(any());
        verify(bookingService, never()).complete(any(), any());
    }

    private Booking bookingFixture(BookingStatus status, UUID teacherId, UUID learnerId) {
        User teacher = mock(User.class);
        when(teacher.getId()).thenReturn(teacherId);
        when(teacher.getFullName()).thenReturn("Teacher");

        User learner = mock(User.class);
        when(learner.getId()).thenReturn(learnerId);
        when(learner.getFullName()).thenReturn("Learner");

        Skill skill = mock(Skill.class);
        when(skill.getId()).thenReturn(UUID.randomUUID());

        Booking booking = new Booking(
                BOOKING_ID, teacher, learner, skill,
                Instant.now().plusSeconds(60), (short) 30, 30);
        booking.setStatus(status);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());
        return booking;
    }
}
