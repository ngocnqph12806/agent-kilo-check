package com.skillseed.skill.repository;

import com.skillseed.shared.domain.SkillCategory;
import com.skillseed.skill.domain.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Skill> findByCategory(SkillCategory category, Pageable pageable);

    Page<Skill> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
