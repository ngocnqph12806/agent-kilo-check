package com.skillseed.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "user_skills_offered",
    uniqueConstraints = @UniqueConstraint(name = "user_skills_offered_user_id_skill_id_key", columnNames = {"user_id", "skill_id"})
)
public class UserSkillOffered {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private com.skillseed.skill.domain.Skill skill;

    @Column(name = "level", nullable = false)
    private short level;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "hourly_seed_rate", nullable = false)
    private int hourlySeedRate = 60;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserSkillOffered() {
    }

    public UserSkillOffered(UUID id, User user, com.skillseed.skill.domain.Skill skill, short level) {
        this.id = id;
        this.user = user;
        this.skill = skill;
        this.level = level;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public com.skillseed.skill.domain.Skill getSkill() {
        return skill;
    }

    public void setSkill(com.skillseed.skill.domain.Skill skill) {
        this.skill = skill;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHourlySeedRate() {
        return hourlySeedRate;
    }

    public void setHourlySeedRate(int hourlySeedRate) {
        this.hourlySeedRate = hourlySeedRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
