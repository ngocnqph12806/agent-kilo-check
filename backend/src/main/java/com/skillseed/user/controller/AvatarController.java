package com.skillseed.user.controller;

import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.domain.User;
import com.skillseed.user.dto.AvatarUploadResponse;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.service.UserService;
import com.skillseed.user.storage.AvatarStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Avatar upload (T-M34). Accepts multipart with field name {@code file}.
 *
 * <p>Validations:
 * <ul>
 *   <li>MIME type must be in the {@link #ALLOWED_MIME} set</li>
 *   <li>Size must be ≤ {@link #MAX_BYTES} (5 MB)</li>
 * </ul>
 * The file is uploaded to the configured {@link AvatarStorage} (Cloudflare
 * R2 in production) and the resulting URL is persisted on
 * {@link User#getAvatarUrl()}.
 */
@RestController
@RequestMapping("/api/v1/users/me/avatar")
@Tag(name = "Users", description = "Avatar upload")
public class AvatarController {

    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_MIME = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");

    private final UserService userService;
    private final AvatarStorage avatarStorage;

    public AvatarController(UserService userService, AvatarStorage avatarStorage) {
        this.userService = userService;
        this.avatarStorage = avatarStorage;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an avatar (multipart, image/*, <=5MB)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid file (type or size)")
    })
    @Transactional
    public ResponseEntity<AvatarUploadResponse> upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw UserException.badRequest("AVATAR_EMPTY", "Avatar file is required");
        }
        if (file.getSize() > MAX_BYTES) {
            throw UserException.badRequest("AVATAR_TOO_LARGE",
                    "Avatar must be at most 5MB");
        }
        String mime = file.getContentType();
        if (mime == null) {
            mime = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        String normalizedMime = mime.toLowerCase(Locale.ROOT);
        if (!ALLOWED_MIME.contains(normalizedMime)) {
            throw UserException.badRequest("AVATAR_BAD_TYPE",
                    "Avatar must be an image (jpeg, png, webp, gif)");
        }

        String extension = extensionFor(normalizedMime, file.getOriginalFilename());
        byte[] bytes = file.getBytes();
        UUID userId = CurrentUser.requireId();
        String url = avatarStorage.upload(userId.toString(), normalizedMime, extension, bytes);

        User user = userService.loadActiveUser(userId);
        user.setAvatarUrl(url);
        user.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(new AvatarUploadResponse(url));
    }

    private String extensionFor(String mime, String originalFilename) {
        return switch (mime) {
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> {
                if (originalFilename != null && originalFilename.contains(".")) {
                    yield originalFilename.substring(
                            originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
                }
                yield "jpg";
            }
        };
    }
}