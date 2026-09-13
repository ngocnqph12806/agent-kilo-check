package com.skillseed.session.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * After T-M412 the controller is a pure dispatcher — participant
 * authorisation and sender-name resolution live in
 * {@link SessionChatService}, which is mocked here.
 */
class SessionChatControllerTest {

    private SessionChatService sessionChatService;
    private SessionChatController controller;

    private static final UUID BOOKING_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID TEACHER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID LEARNER_ID = UUID.fromString("99999999-8888-7777-6666-555555555555");

    @BeforeEach
    void setUp() {
        sessionChatService = mock(SessionChatService.class);
        controller = new SessionChatController(sessionChatService);
    }

    @Test
    void handleBroadcastsMessageForParticipant() {
        when(sessionChatService.resolveChatContext(eq(BOOKING_ID), eq(LEARNER_ID)))
                .thenReturn(Optional.of(new SessionChatService.ChatContext("Aria")));

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
        verify(sessionChatService, never()).resolveChatContext(any(), any());
    }

    @Test
    void handleDropsMessageFromNonParticipant() {
        when(sessionChatService.resolveChatContext(eq(BOOKING_ID), any()))
                .thenReturn(Optional.empty());

        UUID bystander = UUID.randomUUID();
        SessionChatMessage msg = new SessionChatMessage();
        msg.setBody("Hi");

        SessionChatMessage result = controller.handle(BOOKING_ID, msg,
                (Principal) () -> bystander.toString());
        assertThat(result).isNull();
    }

    @Test
    void handleDropsMessageWithoutPrincipal() {
        SessionChatMessage msg = new SessionChatMessage();
        msg.setBody("Hi");

        SessionChatMessage result = controller.handle(BOOKING_ID, msg, null);
        assertThat(result).isNull();
        verify(sessionChatService, never()).resolveChatContext(any(), any());
    }

    @Test
    void handleUsesSenderIdFromPayloadIfPrincipalMissing() {
        when(sessionChatService.resolveChatContext(eq(BOOKING_ID), eq(TEACHER_ID)))
                .thenReturn(Optional.of(new SessionChatService.ChatContext("Tom")));

        SessionChatMessage msg = new SessionChatMessage();
        msg.setBody("hi from teacher");
        msg.setSenderId(TEACHER_ID);

        SessionChatMessage result = controller.handle(BOOKING_ID, msg, null);

        assertThat(result).isNotNull();
        assertThat(result.getSenderId()).isEqualTo(TEACHER_ID);
        assertThat(result.getSenderName()).isEqualTo("Tom");
    }
}
