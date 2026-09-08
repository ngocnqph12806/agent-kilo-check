package com.skillseed.discover.repository;

import com.skillseed.discover.dto.DiscoverMatchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Read-only queries powering {@code GET /api/v1/discover}. Custom
 * JPQL keeps the join between {@code user_skills_offered} (teachers)
 * and {@code user_skills_wanted} (current user) in a single round trip.
 */
@Repository
public interface DiscoverRepository {

    /**
     * @param currentUserId    the authenticated user (excluded from results)
     * @param wantedSkillIds   skill ids the current user wants to learn
     * @param filterSkillId    optional additional skill filter (intersection with wantedSkillIds)
     * @param language         optional language code (matches {@code ANY(languages)})
     * @param countryCode      optional 2-letter ISO country code
     * @param minRating        optional minimum rating (inclusive)
     * @param timezoneOffset   optional UTC offset in hours (-12..14) — only teachers
     *                         whose {@code timezone} resolves to the same hour
     *                         bucket are returned
     * @param pageable         pagination + sort (sort comes from
     *                         {@code DiscoverSort} per FR-M32)
     */
    Page<DiscoverMatchResponse> findMatches(
            UUID currentUserId,
            List<UUID> wantedSkillIds,
            UUID filterSkillId,
            String language,
            String countryCode,
            BigDecimal minRating,
            Integer timezoneOffset,
            Pageable pageable);
}
