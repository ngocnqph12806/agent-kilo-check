package com.skillseed.user.service;

import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserAvailability;
import com.skillseed.user.dto.AvailabilitySlotRequest;
import com.skillseed.user.dto.AvailabilitySlotResponse;
import com.skillseed.user.dto.ReplaceAvailabilityRequest;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserAvailabilityRepository;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Weekly availability slots (T-M44, FR-M12).
 *
 * <p>{@code PUT /users/me/availability} performs a full bulk replace:
 * existing rows for the user are deleted inside the same transaction
 * and the supplied slots are inserted. Validation enforces:
 * <ul>
 *   <li>{@code endTime > startTime} (also enforced by DB CHECK)</li>
 *   <li>No overlapping slots on the same day-of-week + timezone</li>
 * </ul>
 */
@Service
public class AvailabilityService {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityService.class);

    private final UserRepository userRepository;
    private final UserAvailabilityRepository repository;

    public AvailabilityService(
            UserRepository userRepository,
            UserAvailabilityRepository repository) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponse> list(UUID userId) {
        return repository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(UserAvailability::getDayOfWeek)
                        .thenComparing(UserAvailability::getStartTime))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<AvailabilitySlotResponse> replace(UUID userId, ReplaceAvailabilityRequest req) {
        User user = userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> UserException.notFound("USER_NOT_FOUND",
                        "User not found"));

        List<AvailabilitySlotRequest> slots = req.slots() == null ? List.of() : req.slots();
        validateSlots(slots);

        repository.deleteByUserId(userId);
        Instant now = Instant.now();
        List<UserAvailability> persisted = new ArrayList<>(slots.size());
        for (AvailabilitySlotRequest slot : slots) {
            UserAvailability row = new UserAvailability(
                    UUID.randomUUID(), user,
                    slot.dayOfWeek().shortValue(),
                    slot.startTime(), slot.endTime(), slot.timezone());
            row.setCreatedAt(now);
            persisted.add(repository.save(row));
        }
        log.info("Availability replaced for user={} count={}", userId, persisted.size());
        return persisted.stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateSlots(List<AvailabilitySlotRequest> slots) {
        slots.sort(Comparator.comparing(AvailabilitySlotRequest::dayOfWeek)
                .thenComparing(AvailabilitySlotRequest::startTime));
        for (int i = 0; i < slots.size(); i++) {
            AvailabilitySlotRequest s = slots.get(i);
            if (!s.endTime().isAfter(s.startTime())) {
                throw UserException.badRequest("AVAILABILITY_BAD_RANGE",
                        "endTime must be after startTime");
            }
            if (i + 1 < slots.size()) {
                AvailabilitySlotRequest next = slots.get(i + 1);
                if (s.dayOfWeek().equals(next.dayOfWeek())
                        && s.timezone().equals(next.timezone())
                        && next.startTime().isBefore(s.endTime())) {
                    throw UserException.badRequest("AVAILABILITY_OVERLAP",
                            "Slots overlap on day " + s.dayOfWeek());
                }
            }
        }
    }

    private AvailabilitySlotResponse toResponse(UserAvailability row) {
        return new AvailabilitySlotResponse(
                row.getId(),
                row.getDayOfWeek(),
                row.getStartTime(),
                row.getEndTime(),
                row.getTimezone());
    }
}