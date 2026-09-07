package com.skillseed.session;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.session.client.DailyClient;
import com.skillseed.session.client.DailyDtos;
import com.skillseed.session.dto.SessionRoomResponse;
import com.skillseed.session.exception.SessionException;
import com.skillseed.session.service.SessionService;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.domain.SkillCategory;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DailyClient dailyClient;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(bookingRepository, userRepository, dailyClient, 30L);
    }

    @Test
    void deriveRoomName_stripsDashesAndPrefixesWithSs() {
        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
        String name = SessionService.deriveRoomName(id);
        assertThat(name).isEqualTo("ss-11111111222233334444555555555555");
    }

    @Test
    void extractRoomName_returnsTrailingPathSegment() {
        assertThat(SessionService.extractRoomName("https://team.daily.co/ss-abc123"))
                .isEqualTo("ss-abc123");
        assertThat(SessionService.extractRoomName("ss-abc123")).isEqualTo("ss-abc123");
        assertThat(SessionService.extractRoomName(null)).isNull();
        assertThat(SessionService.extractRoomName("https://team.daily.co/")).isNull();
    }

    @Test
    void createRoom_rejectsNonParticipant() {
        Booking booking = newBooking(UUID.randomUUID(), UUID.randomUUID());
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> sessionService.createRoom(booking.getId(), UUID.randomUUID()))
                .isInstanceOf(SessionException.class)
                .hasMessageContaining("NOT_PARTICIPANT");
    }

    @Test
    void createRoom_rejectsPendingBooking() {
        Booking booking = newBooking(UUID.randomUUID(), UUID.randomUUID());
        booking.setStatus(BookingStatus.PENDING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> sessionService.createRoom(booking.getId(),
                booking.getTeacher().getId()))
                .isInstanceOf(SessionException.class)
                .hasMessageContaining("INVALID_STATE_TRANSITION");
    }

    @Test
    void createRoom_createsRoomWhenNoneExists() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newBooking(teacherId, learnerId);
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(stubUser(teacherId, "Alice")));
        when(dailyClient.isConfigured()).thenReturn(true);

        DailyDtos.RoomResponse room = new DailyDtos.RoomResponse();
        room.setName("ss-room1");
        room.setUrl("https://team.daily.co/ss-room1");
        when(dailyClient.createRoom(eq("ss-room1"), any(Instant.class))).thenReturn(room);
        when(dailyClient.createMeetingToken(eq("Alice"), eq(teacherId.toString()),
                eq(true), any(Instant.class))).thenReturn("token-xyz");

        SessionRoomResponse response = sessionService.createRoom(booking.getId(), teacherId);

        assertThat(response.roomUrl()).isEqualTo("https://team.daily.co/ss-room1");
        assertThat(response.token()).isEqualTo("token-xyz");
        assertThat(response.role()).isEqualTo("owner");
        assertThat(response.expiresAt()).isAfter(booking.getScheduledAt());

        ArgumentCaptor<Booking> saved = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(saved.capture());
        assertThat(saved.getValue().getMeetingUrl()).isEqualTo("https://team.daily.co/ss-room1");
    }

    @Test
    void createRoom_reusesExistingRoom() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newBooking(teacherId, learnerId);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setMeetingUrl("https://team.daily.co/ss-existing");
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(learnerId)).thenReturn(Optional.of(stubUser(learnerId, "Bob")));
        when(dailyClient.createMeetingToken(eq("Bob"), eq(learnerId.toString()),
                eq(false), any(Instant.class))).thenReturn("token-bob");

        SessionRoomResponse response = sessionService.createRoom(booking.getId(), learnerId);

        assertThat(response.roomUrl()).isEqualTo("https://team.daily.co/ss-existing");
        assertThat(response.token()).isEqualTo("token-bob");
        assertThat(response.role()).isEqualTo("guest");
        verify(dailyClient, never()).createRoom(anyString(), any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createRoom_rejectsWhenDailyDisabledAndNoRoom() {
        UUID teacherId = UUID.randomUUID();
        UUID learnerId = UUID.randomUUID();
        Booking booking = newBooking(teacherId, learnerId);
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(dailyClient.isConfigured()).thenReturn(false);

        assertThatThrownBy(() -> sessionService.createRoom(booking.getId(), teacherId))
                .isInstanceOf(SessionException.class)
                .hasMessageContaining("DAILY_DISABLED");
    }

    @Test
    void markMeetingEnded_skipsTerminalStates() {
        Booking booking = newBooking(UUID.randomUUID(), UUID.randomUUID());
        booking.setStatus(BookingStatus.COMPLETED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        sessionService.markMeetingEnded(booking.getId());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void markMeetingEnded_completesInProgressBooking() {
        Booking booking = newBooking(UUID.randomUUID(), UUID.randomUUID());
        booking.setStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        sessionService.markMeetingEnded(booking.getId());

        ArgumentCaptor<Booking> saved = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository, times(1)).save(saved.capture());
        assertThat(saved.getValue().getStatus()).isEqualTo(BookingStatus.COMPLETED);
    }

    @Test
    void markMeetingEnded_silentlyIgnoresUnknownBooking() {
        UUID id = UUID.randomUUID();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());
        sessionService.markMeetingEnded(id);
        verify(bookingRepository, never()).save(any());
    }

    private Booking newBooking(UUID teacherId, UUID learnerId) {
        User teacher = stubUser(teacherId, "Alice");
        User learner = stubUser(learnerId, "Bob");
        Skill skill = new Skill(UUID.randomUUID(), "java", "Java", SkillCategory.TECH);
        return new Booking(UUID.randomUUID(), teacher, learner, skill,
                Instant.now().plusSeconds(3600), (short) 30, 30);
    }

    private User stubUser(UUID id, String fullName) {
        User user = new User(id, fullName.toLowerCase() + "@example.com", fullName);
        user.setTimezone("UTC");
        return user;
    }
}
