package com.skillseed.user.dto;

/**
 * Response body for {@code POST /api/v1/users/me/avatar}.
 */
public record AvatarUploadResponse(String avatarUrl) {
}