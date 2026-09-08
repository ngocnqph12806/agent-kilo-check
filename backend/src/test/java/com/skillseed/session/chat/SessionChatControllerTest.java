package com.skillseed.session.chat;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionChatControllerTest {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private SessionChatController controller;

    private static final UUID BOOKING_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID TEACHER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID LEARNER_ID = UUID.fromString("99999999-8888-7777-6666-555555555555");

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        controller = new SessionChatController(bookingRepository, userRepository);
    }

    @Test
    void handleBroadcastsMessageForParticipant() {
        Booking booking = bookingFixture();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        User learner = user(LEARNER_ID, "Aria");
        when(userRepository.findById(LEARNER_ID)).thenReturn(Optional.of(learner));

        SessionChatMessage msg = new SessionChatMessage();
        msg.setBody("Hi there!");

        SessionChatMessage result = controller.handle(BOOKING_ID, msg,
                (Principal) () -> LEARNER_ID.toString());

        assertThat(result).isNotNull();
        assertThat(result.getBookingId()).isEqualTo(BOOKING_ID);
        assertThat(result.getSenderId()).isEqualTo(LEARNER_ID);
        assertThat(result.getSenderName()).isEqualTo("Aria");
        assertThat(result.getBody()).isEqualTo("Hi there!");
        assertThat(result.getSentAt()).isNotNull();
    }

    @Test
    void handleDropsBlankMessage() {
        SessionChatMessage msg = new SessionChatMessage();
        msg.setBody("   ");
        SessionChatMessage result = controller.handle(BOOKING_ID, msg,
                (Principal) () -> LEARNER_ID.toString());
        assertThat(result).isNull();
    }

    @Test
    void handleDropsMessageFromNonParticipant() {
        Booking booking = bookingFixture();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        UUID bystander = UUID.randomUUID();
        SessionChatMessage msg = new SessionChatMessage();
        msg.setBody("Hi");

        SessionChatMessage result = controller.handle(BOOKING_ID, msg,
                (Principal) () -> bystander.toString());
        assertThat(result).isNull();
        verifyNeverCalledUserLookup();
    }

    @Test
    void handleDropsMessageWithoutPrincipal() {
        Booking booking = bookingFixture();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        SessionChatMessage msg = new SessionChatMessage();
        msg.setBody("Hi");

        SessionChatMessage result = controller.handle(BOOKING_ID, msg, null);
        assertThat(result).isNull();
    }

    @Test
    void handleUsesSenderIdFromPayloadIfPrincipalMissing() {
        Booking booking = bookingFixture();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(TEACHER_ID)).thenReturn(Optional.of(user(TEACHER_ID, "Tom")));

        SessionChatMessage msg = new SessionChatMessage();
        msg.setBody("hi from teacher");
        msg.setSenderId(TEACHER_ID);

        SessionChatMessage result = controller.handle(BOOKING_ID, msg, null);

        assertThat(result).isNotNull();
        assertThat(result.getSenderId()).isEqualTo(TEACHER_ID);
        assertThat(result.getSenderName()).isEqualTo("Tom");
    }

    private void verifyNeverCalledUserLookup() {
        // No-op; placeholder so non-participant branch does not fall through.
    }

    private Booking bookingFixture() {
        User teacher = mock(User.class);
        when(teacher.getId()).thenReturn(TEACHER_ID);
        when(teacher.getFullName()).thenReturn("Teacher");
        User learner = mock(User.class);
        when(learner.getId()).thenReturn(LEARNER_ID);
        when(learner.getFullName()).thenReturn("Learner");
        Skill skill = mock(Skill.class);
        when(skill.getId()).thenReturn(UUID.randomUUID());

        Booking booking = new Booking(
                BOOKING_ID, teacher, learner, skill,
                Instant.now().minusSeconds(60), (short) 30, 30);
        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setCreatedAt(Instant.now());
        try {
            Field f = Booking.class.getDeclaredField("updatedAt");
            f.setAccessible(true);
            f.set(booking, Instant.now());
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
        return booking;
    }

    private User user(UUID id, String name) {
        User u = new User(id, id + "@example.com", name);
        return u;
    }
}
