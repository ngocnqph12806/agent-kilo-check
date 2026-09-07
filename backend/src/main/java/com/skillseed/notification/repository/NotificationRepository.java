package com.skillseed.notification.domain;

import com.skillseed.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Notification> findByUserAndReadAtIsNullOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUserAndReadAtIsNull(User user);
}
