package com.skillseed.user.repository;

import com.skillseed.user.domain.UserSkillWanted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSkillWantedRepository extends JpaRepository<UserSkillWanted, UUID> {

    List<UserSkillWanted> findByUserId(UUID userId);

    List<UserSkillWanted> findBySkillId(UUID skillId);
}
