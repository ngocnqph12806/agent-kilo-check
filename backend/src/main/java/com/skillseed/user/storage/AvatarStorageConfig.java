package com.skillseed.user.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Wires an {@link AvatarStorage} implementation. When
 * {@code storage.r2.*} properties are set, the real Cloudflare R2
 * backend is registered; otherwise a local-filesystem fallback is
 * used (dev/test).
 */
@Configuration
public class AvatarStorageConfig {

    private static final Logger log = LoggerFactory.getLogger(AvatarStorageConfig.class);

    @Bean
    @ConditionalOnExpression(
            "'${storage.r2.endpoint:}' != '' && '${storage.r2.bucket:}' != ''"
                    + " && '${storage.r2.access-key:}' != ''"
                    + " && '${storage.r2.secret-key:}' != ''")
    public AvatarStorage r2AvatarStorage(
            @Value("${storage.r2.endpoint}") String endpoint,
            @Value("${storage.r2.bucket}") String bucket,
            @Value("${storage.r2.access-key}") String accessKey,
            @Value("${storage.r2.secret-key}") String secretKey,
            @Value("${storage.r2.public-base-url}") String publicBaseUrl,
            @Value("${storage.r2.region:auto}") String region) {
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region.equals("auto") ? "us-east-1" : region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        return new R2AvatarStorage(client, bucket, publicBaseUrl);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(AvatarStorage.class)
    public AvatarStorage localAvatarStorage(
            @Value("${storage.local-dir:/tmp/skillseed-avatars}") String dir) {
        return new LocalAvatarStorage(dir);
    }

    /** Cloudflare R2 (S3-compatible) implementation. */
    public static final class R2AvatarStorage implements AvatarStorage {

        private final S3Client client;
        private final String bucket;
        private final String publicBaseUrl;

        public R2AvatarStorage(S3Client client, String bucket, String publicBaseUrl) {
            this.client = Objects.requireNonNull(client);
            this.bucket = bucket;
            this.publicBaseUrl = publicBaseUrl.endsWith("/")
                    ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                    : publicBaseUrl;
        }

        @Override
        public String upload(String userId, String contentType, String extension, byte[] data) {
            String key = "avatars/" + userId + "/"
                    + Instant.now().toEpochMilli() + "-" + UUID.randomUUID()
                    + "." + extension.toLowerCase(Locale.ROOT);
            client.putObject(PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build(), RequestBody.fromBytes(data));
            String url = publicBaseUrl + "/" + key;
            log.info("Uploaded avatar to R2 key={}", key);
            return url;
        }
    }

    /** Filesystem fallback for dev/test (writes under storage.local-dir). */
    public static final class LocalAvatarStorage implements AvatarStorage {

        private static final Logger log = LoggerFactory.getLogger(LocalAvatarStorage.class);
        private final Path root;

        public LocalAvatarStorage(String dir) {
            this.root = Paths.get(dir);
            try {
                Files.createDirectories(root);
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "Cannot create avatar storage dir: " + root, ex);
            }
        }

        @Override
        public String upload(String userId, String contentType, String extension, byte[] data) {
            String fileName = userId + "-" + Instant.now().toEpochMilli() + "-"
                    + UUID.randomUUID() + "." + extension.toLowerCase(Locale.ROOT);
            Path file = root.resolve(fileName);
            try {
                Files.write(file, data);
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to write avatar to " + file, ex);
            }
            String url = file.toUri().toString();
            log.info("Stored avatar locally at {}", url);
            return url;
        }
    }
}