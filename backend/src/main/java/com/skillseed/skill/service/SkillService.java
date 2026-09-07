package com.skillseed.skill.service;

import com.skillseed.shared.domain.SkillCategory;
import com.skillseed.shared.domain.SkillStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.dto.CreateCustomSkillRequest;
import com.skillseed.skill.dto.SkillResponse;
import com.skillseed.skill.exception.SkillException;
import com.skillseed.skill.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Skill taxonomy service: search, create custom skills (pending admin
 * review). Read endpoints always exclude {@code rejected} and
 * {@code pending_review} skills from public results.
 */
@Service
public class SkillService {

    private static final Logger log = LoggerFactory.getLogger(SkillService.class);
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Pattern NON_SLUG = Pattern.compile("[^a-z0-9]+");

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Searches the skill taxonomy.
     *
     * @param query     substring match on {@code name} (case-insensitive); null/blank → no name filter
     * @param category  optional category filter
     * @param page      0-indexed page number (clamped to >= 0)
     * @param size      page size (clamped to 1..{@value #MAX_PAGE_SIZE}, default {@value #DEFAULT_PAGE_SIZE})
     */
    @Transactional(readOnly = true)
    public SkillPage search(String query, SkillCategory category, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by("name").ascending());

        boolean hasQuery = query != null && !query.isBlank();
        Page<Skill> result;
        if (hasQuery && category != null) {
            result = skillRepository
                    .findByNameContainingIgnoreCaseAndCategory(query.trim(), category, pageable);
        } else if (hasQuery) {
            result = skillRepository.findByNameContainingIgnoreCase(query.trim(), pageable);
        } else if (category != null) {
            result = skillRepository.findByCategory(category, pageable);
        } else {
            result = skillRepository.findAll(pageable);
        }

        List<SkillResponse> content = result.getContent().stream()
                .filter(s -> s.getStatus() == SkillStatus.APPROVED)
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new SkillPage(content, safePage, safeSize,
                result.getTotalElements(), result.getTotalPages());
    }

    /**
     * Returns a single skill by id (any status). 404 if not found.
     */
    @Transactional(readOnly = true)
    public SkillResponse getById(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> SkillException.notFound("SKILL_NOT_FOUND",
                        "Skill not found"));
        return toResponse(skill);
    }

    /**
     * Creates a user-submitted custom skill (FR-M22). Always stored with
     * {@code is_custom=true} and {@code status=pending_review} for admin
     * approval.
     */
    @Transactional
    public SkillResponse createCustomSkill(CreateCustomSkillRequest req) {
        String name = req.name().trim();
        String categoryValue = req.category().toLowerCase(Locale.ROOT);
        SkillCategory category = parseCategory(categoryValue);

        String slug = toSlug(name);
        if (slug.isBlank()) {
            throw SkillException.badRequest("SKILL_SLUG_INVALID",
                    "Skill name produces an empty slug");
        }
        if (skillRepository.existsBySlug(slug)) {
            throw SkillException.conflict("SKILL_SLUG_EXISTS",
                    "A skill with this name already exists");
        }

        Skill skill = new Skill(UUID.randomUUID(), slug, name, category);
        skill.setCustom(true);
        skill.setStatus(SkillStatus.PENDING_REVIEW);
        skill.setCreatedAt(java.time.Instant.now());
        if (req.parentId() != null) {
            Skill parent = skillRepository.findById(req.parentId())
                    .orElseThrow(() -> SkillException.notFound("PARENT_NOT_FOUND",
                            "Parent skill not found"));
            skill.setParent(parent);
        }
        Skill saved = skillRepository.save(skill);
        log.info("Custom skill created id={} slug={} (pending_review)", saved.getId(), slug);
        return toResponse(saved);
    }

    SkillResponse toResponse(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getSlug(),
                skill.getName(),
                skill.getCategory().getDbValue(),
                skill.isCustom(),
                skill.getStatus().getDbValue(),
                skill.getParentId());
    }

    private SkillCategory parseCategory(String value) {
        for (SkillCategory c : SkillCategory.values()) {
            if (c.getDbValue().equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw SkillException.badRequest("SKILL_CATEGORY_INVALID",
                "Unknown category: " + value);
    }

    /**
     * Converts a free-form name to a URL-safe slug: lowercase ASCII,
     * non-alphanumerics collapsed to a single hyphen, trimmed.
     */
    static String toSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String lowered = normalized.toLowerCase(Locale.ROOT);
        String hyphenated = NON_SLUG.matcher(lowered).replaceAll("-");
        return hyphenated.replaceAll("(^-+)|(-+$)", "");
    }

    /** Paginated response wrapper. */
    public record SkillPage(
            List<SkillResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages) {
    }
}