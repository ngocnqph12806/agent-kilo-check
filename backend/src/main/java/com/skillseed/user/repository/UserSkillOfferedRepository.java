package com.skillseed.user.repository;

import com.skillseed.user.domain.UserSkillOffered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSkillOfferedRepository extends JpaRepository<UserSkillOffered, UUID> {

    List<UserSkillOffered> findByUserId(UUID userId);

    List<UserSkillOffered> findByUserIdAndActiveTrue(UUID userId);

    Optional<UserSkillOffered> findByUserIdAndSkillId(UUID userId, UUID skillId);

    List<UserSkillOffered> findBySkillIdAndActiveTrue(UUID skillId);

    /**
     * Bulk-load the offered-skill rows for a page of users restricted to
     * a specific skill set — used by Discover to avoid a {@code findAll()}
     * scan of the whole table when projecting top skills per match.
     */
    List<UserSkillOffered> findByUserIdIn(java.util.Collection<UUID> userIds);

    List<UserSkillOffered> findByUserIdInAndSkillIdIn(
            java.util.Collection<UUID> userIds,
            java.util.Collection<UUID> skillIds);
}
