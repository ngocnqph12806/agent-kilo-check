package com.skillseed.session.repository;

import com.skillseed.session.domain.SessionIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionIncidentRepository extends JpaRepository<SessionIncident, UUID> {
}
