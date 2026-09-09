package com.skillseed.waitlist.repository;

import com.skillseed.waitlist.domain.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitlistRepository extends JpaRepository<WaitlistEntry, UUID> {

    Optional<WaitlistEntry> findByEmailLower(String emailLower);

    boolean existsByEmailLower(String emailLower);
}
