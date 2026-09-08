package com.skillseed.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillseed.booking.domain.IdempotencyKey;
import com.skillseed.booking.exception.BookingException;
import com.skillseed.booking.repository.IdempotencyKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Idempotency helper for mutating endpoints (FR-M47 / T-M101).
 *
 * <p>First time a (user, endpoint, key) tuple is seen, the supplied
 * action is executed and its {@link ResponseEntity} is cached (body
 * serialised via Jackson, status code persisted). Subsequent calls
 * with the same key replay the cached response — never re-executing
 * the side-effecting action. If the client sends the same key with a
 * <em>different</em> request hash, the helper throws
 * {@link BookingException#conflict} with code
 * {@code IDEMPOTENCY_KEY_REUSED}.
 */
@Service
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);

    private final IdempotencyKeyRepository repository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyKeyRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public <T> ResponseEntity<T> executeOnce(String key, UUID userId, String endpoint,
                                             Object requestPayload,
                                             Class<T> responseType,
                                             Supplier<ResponseEntity<T>> action) {
        if (key == null || key.isBlank()) {
            return action.get();
        }
        String requestHash = sha256(requestPayload);

        var existing = repository.findById(key).orElse(null);
        if (existing != null) {
            if (!existing.getUserId().equals(userId)
                    || !existing.getEndpoint().equals(endpoint)) {
                throw BookingException.conflict("IDEMPOTENCY_KEY_REUSED",
                        "Idempotency-Key was previously used for a different call");
            }
            if (!existing.getRequestHash().equals(requestHash)) {
                throw BookingException.conflict("IDEMPOTENCY_KEY_REUSED",
                        "Idempotency-Key was previously used with a different payload");
            }
            log.info("Idempotency replay: key={} endpoint={} user={}",
                    key, endpoint, userId);
            try {
                T body = objectMapper.readValue(existing.getResponseBody(), responseType);
                return ResponseEntity.status(existing.getResponseStatus()).body(body);
            } catch (JsonProcessingException ex) {
                throw BookingException.conflict("IDEMPOTENCY_REPLAY_FAILED",
                        "Cached idempotent response could not be deserialised");
            }
        }

        ResponseEntity<T> response = action.get();
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                IdempotencyKey record = new IdempotencyKey(
                        key,
                        userId,
                        endpoint,
                        requestHash,
                        response.getStatusCode().value(),
                        objectMapper.writeValueAsString(response.getBody()),
                        Instant.now());
                try {
                    repository.save(record);
                } catch (DataIntegrityViolationException race) {
                    // Lost the race against a concurrent identical request;
                    // the winner stored first wins — fall through and let
                    // the caller treat the freshly executed action as success.
                    log.info("Idempotency race on key={}, winner already stored",
                            key);
                }
            } catch (JsonProcessingException ex) {
                log.warn("Could not serialise idempotent response body for key={}",
                        key, ex);
            }
        }
        return response;
    }

    private String sha256(Object payload) {
        try {
            String json = objectMapper.writeValueAsString(
                    payload == null ? "" : payload);
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(json.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            return Integer.toHexString(payload == null ? 0 : payload.hashCode());
        }
    }

    public static <T> ResponseEntity<T> replayed(HttpStatus status, T body) {
        return ResponseEntity.status(status).body(body);
    }
}
