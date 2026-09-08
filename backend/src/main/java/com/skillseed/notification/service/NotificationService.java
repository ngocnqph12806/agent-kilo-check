package com.skillseed.notification.service;

import com.skillseed.notification.domain.Notification;
import com.skillseed.notification.domain.NotificationRepository;
import com.skillseed.notification.domain.NotificationType;
import com.skillseed.notification.dto.NotificationPageResponse;
import com.skillseed.notification.dto.NotificationResponse;
import com.skillseed.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Application service for the notification module. Reads the inbox
 * for the current user, exposes the unread count, and supports marking
 * individual notifications read. Writes are exposed via
 * {@link #publish(User, NotificationType, Map)} so other modules
 * (booking, rating, system) can fan out events without depending on
 * each other.
 */
@Service
public class NotificationService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Create a new notification for {@code recipient}. Skipped silently if
     * the recipient is null (defensive — should not happen in practice).
     */
    @Transactional
    public Notification publish(User recipient, NotificationType type, Map<String, Object> payload) {
        if (recipient == null) {
            return null;
        }
        Notification n = new Notification(UUID.randomUUID(), recipient, type,
                payload == null ? Map.of() : payload);
        return notificationRepository.save(n);
    }

    @Transactional(readOnly = true)
    public NotificationPageResponse list(User user, boolean unreadOnly, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(safePage, safeSize);

        Page<Notification> result = unreadOnly
                ? notificationRepository.findByUserAndReadAtIsNullOrderByCreatedAtDesc(user, pageable)
                : notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        long unreadCount = notificationRepository.countByUserAndReadAtIsNull(user);

        return new NotificationPageResponse(
                result.getContent().stream().map(this::toDto).toList(),
                safePage,
                safeSize,
                result.getTotalElements(),
                result.getTotalPages(),
                unreadCount);
    }

    @Transactional(readOnly = true)
    public long unreadCount(User user) {
        return notificationRepository.countByUserAndReadAtIsNull(user);
    }

    @Transactional
    public void markRead(User user, UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!n.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Notification does not belong to caller");
        }
        if (n.isUnread()) {
            n.markRead(Instant.now());
            notificationRepository.save(n);
        }
    }

    @Transactional
    public int markAllRead(User user) {
        Page<Notification> unread = notificationRepository
                .findByUserAndReadAtIsNullOrderByCreatedAtDesc(user, PageRequest.of(0, MAX_PAGE_SIZE));
        Instant now = Instant.now();
        unread.forEach(n -> n.markRead(now));
        notificationRepository.saveAll(unread);
        return unread.getNumberOfElements();
    }

    private NotificationResponse toDto(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType().getDbValue(),
                n.getPayload(),
                n.isUnread(),
                n.getCreatedAt(),
                n.getReadAt());
    }
}
