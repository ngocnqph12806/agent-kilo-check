package com.skillseed.discover.service;

import com.skillseed.discover.dto.DiscoverMatchResponse;
import com.skillseed.discover.dto.DiscoverPageResponse;
import com.skillseed.discover.repository.DiscoverRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserSkillWanted;
import com.skillseed.user.repository.UserSkillWantedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public DiscoverPageResponse discover(User currentUser, UUID skillFilter, String language,
            String countryCode, BigDecimal minRating, int page, int size) {
        List<UUID> wantedSkillIds = userSkillWantedRepository.findByUserId(currentUser.getId())
                .stream()
                .map(UserSkillWanted::getSkill)
                .map(skill -> skill.getId())
                .toList();

        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(safePage, safeSize);

        Page<DiscoverMatchResponse> result = discoverRepository.findMatches(
                currentUser.getId(),
                wantedSkillIds,
                skillFilter,
                language,
                countryCode,
                minRating,
                pageable);

        return new DiscoverPageResponse(
                result.getContent(),
                safePage,
                safeSize,
                result.getTotalElements(),
                result.getTotalPages());
    }
}
