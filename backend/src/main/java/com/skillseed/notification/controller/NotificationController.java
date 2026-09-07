package com.skillseed.notification.controller;

import com.skillseed.notification.dto.NotificationPageResponse;
import com.skillseed.notification.service.NotificationService;
import com.skillseed.shared.exception.ApiErrorResponse;
import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.domain.User;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * In-app notification inbox endpoints. Mounted under {@code /api/v1/notifications}
 * and protected by the standard JWT auth filter (any authenticated user).
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "In-app notification inbox")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService,
            UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "List the current user's notification inbox")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inbox page")
    })
    public ResponseEntity<NotificationPageResponse> myInbox(
            @RequestParam(name = "unreadOnly", defaultValue = "false") boolean unreadOnly,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        User user = loadCurrentUser();
        return ResponseEntity.ok(notificationService.list(user, unreadOnly, page, size));
    }

    @GetMapping("/me/unread-count")
    @Operation(summary = "Count of unread notifications for the current user")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        User user = loadCurrentUser();
        return ResponseEntity.ok(Map.of("count", notificationService.unreadCount(user)));
    }

    @PostMapping("/me/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<Map<String, String>> markRead(@PathVariable("id") UUID id) {
        User user = loadCurrentUser();
        try {
            notificationService.markRead(user, id);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "NOTIFICATION_NOT_FOUND"));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "FORBIDDEN"));
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/me/read-all")
    @Operation(summary = "Mark every unread notification as read")
    public ResponseEntity<Map<String, Integer>> markAllRead() {
        User user = loadCurrentUser();
        int count = notificationService.markAllRead(user);
        return ResponseEntity.ok(Map.of("markedRead", count));
    }

    private User loadCurrentUser() {
        return userRepository.findById(CurrentUser.requireId())
                .orElseThrow(() -> UserException.notFound("USER_NOT_FOUND",
                        "Authenticated user no longer exists"));
    }
}
