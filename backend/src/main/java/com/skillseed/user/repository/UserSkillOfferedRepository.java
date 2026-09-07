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
}
