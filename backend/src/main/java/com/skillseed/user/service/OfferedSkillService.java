package com.skillseed.user.service;

import com.skillseed.shared.domain.SkillStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.repository.SkillRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserSkillOffered;
import com.skillseed.user.dto.CreateOfferedSkillRequest;
import com.skillseed.user.dto.OfferedSkillResponse;
import com.skillseed.user.dto.UpdateOfferedSkillRequest;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.user.repository.UserSkillOfferedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * CRUD service for {@link UserSkillOffered} (T-M42, FR-M10). Each row
 * is scoped to a single user; cross-user access is rejected with
 * {@code NOT_OWNER}.
 */
@Service
public class OfferedSkillService {

    private static final Logger log = LoggerFactory.getLogger(OfferedSkillService.class);

    private final UserRepository userRepository;
    private final UserSkillOfferedRepository repository;
    private final SkillRepository skillRepository;

    public OfferedSkillService(
            UserRepository userRepository,
            UserSkillOfferedRepository repository,
            SkillRepository skillRepository) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<OfferedSkillResponse> list(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OfferedSkillResponse create(UUID userId, CreateOfferedSkillRequest req) {
        User user = loadUser(userId);
        Skill skill = loadApprovedSkill(req.skillId());
        if (repository.findByUserIdAndSkillId(userId, skill.getId()).isPresent()) {
            throw UserException.conflict("OFFERED_SKILL_EXISTS",
                    "You already offer this skill");
        }
        UserSkillOffered row = new UserSkillOffered(
                UUID.randomUUID(), user, skill, req.level());
        if (req.yearsExperience() != null) {
            row.setYearsExperience(req.yearsExperience());
        }
        if (req.description() != null) {
            row.setDescription(req.description());
        }
        if (req.hourlySeedRate() != null) {
            row.setHourlySeedRate(req.hourlySeedRate());
        }
        Instant now = Instant.now();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        UserSkillOffered saved = repository.save(row);
        log.info("Offered skill added user={} skill={}", userId, skill.getId());
        return toResponse(saved);
    }

    @Transactional
    public OfferedSkillResponse update(UUID userId, UUID rowId, UpdateOfferedSkillRequest req) {
        UserSkillOffered row = loadOwned(userId, rowId);
        if (req.level() != null) {
            row.setLevel(req.level());
        }
        if (req.yearsExperience() != null) {
            row.setYearsExperience(req.yearsExperience());
        }
        if (req.description() != null) {
            row.setDescription(req.description());
        }
        if (req.hourlySeedRate() != null) {
            row.setHourlySeedRate(req.hourlySeedRate());
        }
        if (req.active() != null) {
            row.setActive(req.active());
        }
        row.setUpdatedAt(Instant.now());
        UserSkillOffered saved = repository.save(row);
        log.info("Offered skill updated user={} id={}", userId, rowId);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID userId, UUID rowId) {
        UserSkillOffered row = loadOwned(userId, rowId);
        repository.delete(row);
        log.info("Offered skill deleted user={} id={}", userId, rowId);
    }

    private UserSkillOffered loadOwned(UUID userId, UUID rowId) {
        UserSkillOffered row = repository.findById(rowId)
                .orElseThrow(() -> UserException.notFound("OFFERED_SKILL_NOT_FOUND",
                        "Offered skill not found"));
        if (!row.getUser().getId().equals(userId)) {
            throw UserException.badRequest("NOT_OWNER",
                    "You do not own this offered skill row");
        }
        return row;
    }

    private User loadUser(UUID userId) {
        return userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> UserException.notFound("USER_NOT_FOUND",
                        "User not found"));
    }

    private Skill loadApprovedSkill(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> UserException.badRequest("SKILL_NOT_FOUND",
                        "Skill not found"));
        if (skill.getStatus() != SkillStatus.APPROVED) {
            throw UserException.badRequest("SKILL_NOT_APPROVED",
                    "Skill is not available for offering");
        }
        return skill;
    }

    OfferedSkillResponse toResponse(UserSkillOffered row) {
        Skill skill = row.getSkill();
        return new OfferedSkillResponse(
                row.getId(),
                skill.getId(),
                skill.getSlug(),
                skill.getName(),
                skill.getCategory().getDbValue(),
                row.getLevel(),
                row.getYearsExperience(),
                row.getDescription(),
                row.getHourlySeedRate(),
                row.isActive(),
                row.getCreatedAt(),
                row.getUpdatedAt());
    }
}