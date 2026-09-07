package com.skillseed.notification.service;

import com.skillseed.notification.domain.Notification;
import com.skillseed.notification.domain.NotificationRepository;
import com.skillseed.notification.domain.NotificationType;
import com.skillseed.notification.dto.NotificationPageResponse;
import com.skillseed.shared.domain.AuthProvider;
import com.skillseed.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link NotificationService}. Covers publishing,
 * listing (read + unread), mark-read + mark-all-read, and ownership
 * enforcement for mark-read.
 */
class NotificationServiceTest {

    private NotificationRepository repo;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        repo = mock(NotificationRepository.class);
        service = new NotificationService(repo);
    }

    private static User user(UUID id) {
        User u = new User(id, "alice@example.com", "Alice");
        u.setAuthProvider(AuthProvider.EMAIL);
        u.setVerified(true);
        return u;
    }

    @Test
    void publishPersistsNewNotification() {
        User recipient = user(UUID.randomUUID());
        when(repo.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification saved = service.publish(recipient, NotificationType.WELCOME,
                Map.of("name", "Alice"));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(captor.capture());
        Notification n = captor.getValue();
        assertThat(n.getUser()).isEqualTo(recipient);
        assertThat(n.getType()).isEqualTo(NotificationType.WELCOME);
        assertThat(n.getPayload()).containsEntry("name", "Alice");
        assertThat(n.isUnread()).isTrue();
        assertThat(saved).isNotNull();
    }

    @Test
    void publishOnNullRecipientIsNoOp() {
        Notification result = service.publish(null, NotificationType.SYSTEM, Map.of());
        assertThat(result).isNull();
        verify(repo, never()).save(any());
    }

    @Test
    void listClampsPageAndSizeAndReturnsUnreadCount() {
        User u = user(UUID.randomUUID());
        Notification n = new Notification(UUID.randomUUID(), u, NotificationType.SYSTEM,
                Map.of("hello", "world"));
        when(repo.findByUserOrderByCreatedAtDesc(eqUser(u), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(n)));
        when(repo.countByUserAndReadAtIsNull(u)).thenReturn(3L);

        NotificationPageResponse response = service.list(u, false, -2, 9999);

        assertThat(response.items()).hasSize(1);
        assertThat(response.unreadCount()).isEqualTo(3L);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findByUserOrderByCreatedAtDesc(eqUser(u), pageableCaptor.capture());
        Pageable used = pageableCaptor.getValue();
        assertThat(used.getPageNumber()).isZero();
        assertThat(used.getPageSize()).isEqualTo(100);
    }

    @Test
    void listWithUnreadOnlyUsesUnreadQuery() {
        User u = user(UUID.randomUUID());
        when(repo.findByUserAndReadAtIsNullOrderByCreatedAtDesc(eqUser(u), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(repo.countByUserAndReadAtIsNull(u)).thenReturn(0L);

        service.list(u, true, 0, 20);

        verify(repo).findByUserAndReadAtIsNullOrderByCreatedAtDesc(eqUser(u),
                PageRequest.of(0, 20));
    }

    @Test
    void markReadFlipsUnreadToRead() {
        User u = user(UUID.randomUUID());
        Notification n = new Notification(UUID.randomUUID(), u, NotificationType.RATING_PROMPT,
                Map.of());
        when(repo.findById(n.getId())).thenReturn(java.util.Optional.of(n));

        service.markRead(u, n.getId());

        assertThat(n.isUnread()).isFalse();
        assertThat(n.getReadAt()).isNotNull();
        verify(repo).save(n);
    }

    @Test
    void markReadOnMissingThrows() {
        User u = user(UUID.randomUUID());
        when(repo.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.markRead(u, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void markReadRejectsForeignNotification() {
        User owner = user(UUID.randomUUID());
        User attacker = user(UUID.randomUUID());
        Notification n = new Notification(UUID.randomUUID(), owner, NotificationType.SYSTEM,
                Map.of());
        when(repo.findById(n.getId())).thenReturn(java.util.Optional.of(n));

        assertThatThrownBy(() -> service.markRead(attacker, n.getId()))
                .isInstanceOf(SecurityException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void markAllReadMarksEveryUnread() {
        User u = user(UUID.randomUUID());
        Notification a = new Notification(UUID.randomUUID(), u, NotificationType.SYSTEM, Map.of());
        Notification b = new Notification(UUID.randomUUID(), u, NotificationType.SYSTEM, Map.of());
        Page<Notification> unread = new PageImpl<>(List.of(a, b));
        when(repo.findByUserAndReadAtIsNullOrderByCreatedAtDesc(eqUser(u), any(Pageable.class)))
                .thenReturn(unread);

        int count = service.markAllRead(u);

        assertThat(count).isEqualTo(2);
        assertThat(a.isUnread()).isFalse();
        assertThat(b.isUnread()).isFalse();
        verify(repo).saveAll(any());
    }

    private static User eqUser(User expected) {
        return org.mockito.ArgumentMatchers.argThat(u -> u.getId().equals(expected.getId()));
    }
}
