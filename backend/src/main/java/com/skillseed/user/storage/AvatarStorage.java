package com.skillseed.user.storage;

/**
 * Abstraction over object storage used for user-uploaded media
 * (avatars, future session assets). Backed by Cloudflare R2 in
 * production; a local filesystem fallback is provided so the app
 * boots without R2 credentials (dev/test).
 */
public interface AvatarStorage {

    /**
     * Uploads avatar bytes and returns the publicly accessible URL.
     *
     * @param userId      owner of the avatar (used to namespace the key)
     * @param contentType MIME type of the upload (e.g. {@code image/png})
     * @param extension   file extension without dot (e.g. {@code png})
     * @param data        raw bytes
     */
    String upload(String userId, String contentType, String extension, byte[] data);
}