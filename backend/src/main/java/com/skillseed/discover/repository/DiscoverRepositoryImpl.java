package com.skillseed.discover.repository;

import com.skillseed.discover.dto.DiscoverMatchResponse;
import com.skillseed.discover.dto.DiscoverMatchResponse.MatchedSkill;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserSkillOffered;
import com.skillseed.user.repository.UserSkillOfferedRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPQL implementation of {@link DiscoverRepositoryCustom}. Strategy:
 * (1) run a single DISTINCT-user query to pick candidate teachers; (2)
 * batch-load their offered skills; (3) build the DTO tree in Java so
 * the matched-skill list stays nested without an additional round trip.
 */
@Repository
public class DiscoverRepositoryImpl implements DiscoverRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private final UserSkillOfferedRepository userSkillOfferedRepository;

    public DiscoverRepositoryImpl(UserSkillOfferedRepository userSkillOfferedRepository) {
        this.userSkillOfferedRepository = userSkillOfferedRepository;
    }

    @Override
    public Page<DiscoverMatchResponse> findMatches(
            UUID currentUserId,
            List<UUID> wantedSkillIds,
            UUID filterSkillId,
            String language,
            String countryCode,
            BigDecimal minRating,
            Pageable pageable) {

        if (wantedSkillIds == null || wantedSkillIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<UUID> effectiveSkills = filterSkillId == null
                ? wantedSkillIds
                : wantedSkillIds.contains(filterSkillId)
                        ? List.of(filterSkillId)
                        : List.of();

        if (effectiveSkills.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        StringBuilder jpql = new StringBuilder("""
                SELECT DISTINCT u FROM User u
                  JOIN UserSkillOffered uso ON uso.user = u
                 WHERE u.id <> :currentUserId
                   AND u.deletedAt IS NULL
                   AND u.verified = true
                   AND uso.active = true
                   AND uso.skill.id IN :skillIds
                """);
        if (language != null && !language.isBlank()) {
            jpql.append(" AND :lang = ANY(u.languages) ");
        }
        if (countryCode != null && !countryCode.isBlank()) {
            jpql.append(" AND u.countryCode = :country ");
        }
        if (minRating != null) {
            jpql.append(" AND u.ratingAvg >= :minRating ");
        }
        jpql.append(" ORDER BY u.ratingAvg DESC, u.sessionsCompleted DESC ");

        var query = em.createQuery(jpql.toString(), User.class)
                .setParameter("currentUserId", currentUserId)
                .setParameter("skillIds", effectiveSkills);
        if (language != null && !language.isBlank()) {
            query.setParameter("lang", language);
        }
        if (countryCode != null && !countryCode.isBlank()) {
            query.setParameter("country", countryCode.toUpperCase());
        }
        if (minRating != null) {
            query.setParameter("minRating", minRating);
        }

        long total = query.getResultList().size();
        List<User> users = query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        if (users.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<UUID> userIds = users.stream().map(User::getId).toList();
        Map<UUID, List<UserSkillOffered>> offeredByUser = userSkillOfferedRepository
                .findAll().stream()
                .filter(o -> userIds.contains(o.getUser().getId()))
                .filter(o -> effectiveSkills.contains(o.getSkill().getId()))
                .collect(Collectors.groupingBy(o -> o.getUser().getId()));

        List<DiscoverMatchResponse> items = users.stream()
                .map(u -> toMatchResponse(u, offeredByUser.getOrDefault(u.getId(), List.of())))
                .toList();

        return new PageImpl<>(items, pageable, total);
    }

    private DiscoverMatchResponse toMatchResponse(User user, List<UserSkillOffered> offered) {
        List<MatchedSkill> skills = offered.stream()
                .sorted(Comparator
                        .comparingInt((UserSkillOffered o) -> -o.getLevel())
                        .thenComparingInt(o -> o.getHourlySeedRate()))
                .limit(3)
                .map(o -> new MatchedSkill(
                        o.getSkill().getId(),
                        o.getSkill().getSlug(),
                        o.getSkill().getName(),
                        o.getSkill().getCategory().getDbValue(),
                        o.getLevel(),
                        o.getHourlySeedRate()))
                .toList();

        return new DiscoverMatchResponse(
                user.getId(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getCountryCode(),
                user.getLanguages() == null ? List.of() : List.of(user.getLanguages()),
                user.getBio(),
                user.getRatingAvg(),
                user.getSessionsCompleted(),
                skills.size(),
                skills);
    }
}
