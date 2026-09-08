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
     * @param pageable         pagination + sort (sort is overridden internally to
     *                         {@code rating_avg DESC, sessions_completed DESC})
     */
    Page<DiscoverMatchResponse> findMatches(
            UUID currentUserId,
            List<UUID> wantedSkillIds,
            UUID filterSkillId,
            String language,
            String countryCode,
            BigDecimal minRating,
            Pageable pageable);
}
