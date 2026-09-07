package com.skillseed.user.repository;

import com.skillseed.user.domain.UserAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAvailabilityRepository extends JpaRepository<UserAvailability, UUID> {

    List<UserAvailability> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
