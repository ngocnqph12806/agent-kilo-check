package com.skillseed.notification.controller;

import com.skillseed.notification.dto.NotificationPageResponse;
import com.skillseed.notification.service.NotificationService;
import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.domain.User;
import com.skillseed.user.service.UserService;
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
 *
 * <p>After T-M412 this controller no longer touches {@code UserRepository}
 * directly — current-user loading is delegated to
 * {@link UserService#loadActiveUser(UUID)} and error mapping to
 * {@link com.skillseed.shared.exception.GlobalExceptionHandler} via
 * {@link com.skillseed.shared.exception.DomainException}.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "In-app notification inbox")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService,
            UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
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
        User user = userService.loadActiveUser(CurrentUser.requireId());
        return ResponseEntity.ok(notificationService.list(user, unreadOnly, page, size));
    }

    @GetMapping("/me/unread-count")
    @Operation(summary = "Count of unread notifications for the current user")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        User user = userService.loadActiveUser(CurrentUser.requireId());
        return ResponseEntity.ok(Map.of("count", notificationService.unreadCount(user)));
    }

    @PostMapping("/me/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<Map<String, String>> markRead(@PathVariable("id") UUID id) {
        User user = userService.loadActiveUser(CurrentUser.requireId());
        // markRead throws DomainException (NOT_FOUND / FORBIDDEN) which is mapped
        // to ApiErrorResponse by GlobalExceptionHandler — no controller-level try/catch.
        notificationService.markRead(user, id);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/me/read-all")
    @Operation(summary = "Mark every unread notification as read")
    public ResponseEntity<Map<String, Integer>> markAllRead() {
        User user = userService.loadActiveUser(CurrentUser.requireId());
        int count = notificationService.markAllRead(user);
        return ResponseEntity.ok(Map.of("markedRead", count));
    }
}
