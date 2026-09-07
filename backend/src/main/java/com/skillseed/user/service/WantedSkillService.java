package com.skillseed.user.service;

import com.skillseed.shared.domain.SkillStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.repository.SkillRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserSkillWanted;
import com.skillseed.user.dto.CreateWantedSkillRequest;
import com.skillseed.user.dto.UpdateWantedSkillRequest;
import com.skillseed.user.dto.WantedSkillResponse;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.user.repository.UserSkillWantedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * CRUD service for {@link UserSkillWanted} (T-M43, FR-M11).
 *
 * <p>No unique constraint on (user_id, skill_id) is enforced at the DB
 * level (a user can want to learn the same skill twice with different
 * priorities/notes), so duplicates are accepted here as designed.
 */
@Service
public class WantedSkillService {

    private static final Logger log = LoggerFactory.getLogger(WantedSkillService.class);

    private final UserRepository userRepository;
    private final UserSkillWantedRepository repository;
    private final SkillRepository skillRepository;

    public WantedSkillService(
            UserRepository userRepository,
            UserSkillWantedRepository repository,
            SkillRepository skillRepository) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<WantedSkillResponse> list(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WantedSkillResponse create(UUID userId, CreateWantedSkillRequest req) {
        User user = loadUser(userId);
        Skill skill = loadApprovedSkill(req.skillId());
        UserSkillWanted row = new UserSkillWanted(UUID.randomUUID(), user, skill);
        row.setPriority(req.priority());
        if (req.targetLevel() != null) {
            row.setTargetLevel(req.targetLevel());
        }
        if (req.notes() != null) {
            row.setNotes(req.notes());
        }
        row.setCreatedAt(Instant.now());
        UserSkillWanted saved = repository.save(row);
        log.info("Wanted skill added user={} skill={}", userId, skill.getId());
        return toResponse(saved);
    }

    @Transactional
    public WantedSkillResponse update(UUID userId, UUID rowId, UpdateWantedSkillRequest req) {
        UserSkillWanted row = loadOwned(userId, rowId);
        if (req.priority() != null) {
            row.setPriority(req.priority());
        }
        if (req.targetLevel() != null) {
            row.setTargetLevel(req.targetLevel());
        }
        if (req.notes() != null) {
            row.setNotes(req.notes());
        }
        UserSkillWanted saved = repository.save(row);
        log.info("Wanted skill updated user={} id={}", userId, rowId);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID userId, UUID rowId) {
        UserSkillWanted row = loadOwned(userId, rowId);
        repository.delete(row);
        log.info("Wanted skill deleted user={} id={}", userId, rowId);
    }

    private UserSkillWanted loadOwned(UUID userId, UUID rowId) {
        UserSkillWanted row = repository.findById(rowId)
                .orElseThrow(() -> UserException.notFound("WANTED_SKILL_NOT_FOUND",
                        "Wanted skill not found"));
        if (!row.getUser().getId().equals(userId)) {
            throw UserException.badRequest("NOT_OWNER",
                    "You do not own this wanted skill row");
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
                    "Skill is not available yet");
        }
        return skill;
    }

    WantedSkillResponse toResponse(UserSkillWanted row) {
        Skill skill = row.getSkill();
        return new WantedSkillResponse(
                row.getId(),
                skill.getId(),
                skill.getSlug(),
                skill.getName(),
                skill.getCategory().getDbValue(),
                row.getPriority(),
                row.getTargetLevel(),
                row.getNotes(),
                row.getCreatedAt());
    }
}