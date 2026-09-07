package com.skillseed.discover.repository;

import com.skillseed.discover.dto.DiscoverMatchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data marker interface so the custom impl
 * {@link DiscoverRepositoryImpl} is auto-detected by component scan.
 */
@Repository
public interface DiscoverRepository extends DiscoverRepositoryCustom {

    @Override
    Page<DiscoverMatchResponse> findMatches(
            UUID currentUserId,
            List<UUID> wantedSkillIds,
            UUID filterSkillId,
            String language,
            String countryCode,
            BigDecimal minRating,
            Pageable pageable);
}
