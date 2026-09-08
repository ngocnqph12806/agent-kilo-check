package com.skillseed.discover.service;

import com.skillseed.discover.dto.DiscoverMatchResponse;
import com.skillseed.discover.dto.DiscoverPageResponse;
import com.skillseed.discover.dto.DiscoverSort;
import com.skillseed.discover.repository.DiscoverRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserSkillWanted;
import com.skillseed.user.repository.UserSkillWantedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class DiscoverService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final DiscoverRepository discoverRepository;
    private final UserSkillWantedRepository userSkillWantedRepository;

    public DiscoverService(DiscoverRepository discoverRepository,
            UserSkillWantedRepository userSkillWantedRepository) {
        this.discoverRepository = discoverRepository;
        this.userSkillWantedRepository = userSkillWantedRepository;
    }

    @Transactional(readOnly = true)
    public DiscoverPageResponse discover(User currentUser,
                                         UUID skillFilter,
                                         String language,
                                         String countryCode,
                                         BigDecimal minRating,
                                         Integer timezoneOffset,
                                         DiscoverSort sort,
                                         int page,
                                         int size) {
        List<UUID> wantedSkillIds = userSkillWantedRepository.findByUserId(currentUser.getId())
                .stream()
                .map(UserSkillWanted::getSkill)
                .map(skill -> skill.getId())
                .toList();
        if (wantedSkillIds.isEmpty()) {
            return new DiscoverPageResponse(List.of(), 0, DEFAULT_PAGE_SIZE, 0, 0);
        }

        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        DiscoverSort safeSort = sort == null ? DiscoverSort.RATING : sort;
        PageRequest pageable = PageRequest.of(safePage, safeSize,
                Sort.by(safeSort.direction(), safeSort.property()));

        Page<DiscoverMatchResponse> result = discoverRepository.findMatches(
                currentUser.getId(),
                wantedSkillIds,
                skillFilter,
                language,
                countryCode,
                minRating,
                timezoneOffset,
                pageable);

        return new DiscoverPageResponse(
                result.getContent(),
                safePage,
                safeSize,
                result.getTotalElements(),
                result.getTotalPages());
    }
}
