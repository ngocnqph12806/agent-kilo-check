package com.skillseed.admin;

import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Stamps the admin user with a BCrypt password hash at boot.
 *
 * <p>Migration V15 inserts the row with a NULL password hash because BCrypt
 * is salted and can't be pre-computed in SQL. This runner resolves the
 * password from {@code ADMIN_PASSWORD} (default {@code SkillSeed!Admin2026})
 * and saves it. Run with overrides:
 *
 * <pre>
 *   ADMIN_EMAIL=admin@example.com \
 *   ADMIN_PASSWORD='change-me-now!' \
 *   ADMIN_FULL_NAME='Trust & Safety' \
 *   ./mvnw spring-boot:run
 * </pre>
 *
 * <p>Safe to run on every boot: it only writes if the row's password_hash
 * is NULL.
 */
@Component
@Order(10) // Run after Flyway has applied V15.
public class AdminSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeedRunner.class);
    private static final String DEFAULT_EMAIL = "admin@skillseed.local";
    private static final String DEFAULT_FULL_NAME = "SkillSeed Admin";
    private static final String DEFAULT_PASSWORD = "SkillSeed!Admin2026";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminFullName;
    private final String adminPassword;

    public AdminSeedRunner(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.email:" + DEFAULT_EMAIL + "}") String adminEmail,
            @Value("${admin.full-name:" + DEFAULT_FULL_NAME + "}") String adminFullName,
            @Value("${admin.password:" + DEFAULT_PASSWORD + "}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminFullName = adminFullName;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User admin = userRepository.findByEmail(adminEmail).orElse(null);
        if (admin == null) {
            log.warn(
                    "Admin seed row not found for {}. V15 should have inserted it.",
                    adminEmail);
            return;
        }
        boolean changed = false;
        if (admin.getPasswordHash() == null || admin.getPasswordHash().isBlank()) {
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            changed = true;
        }
        if (!admin.getFullName().equals(adminFullName)) {
            admin.setFullName(adminFullName);
            changed = true;
        }
        if (changed) {
            userRepository.save(admin);
            log.info(
                    "Admin account '{}' credentials applied (id={}) — rotate the password before exposing to anyone outside dev.",
                    adminEmail, admin.getId());
        }
    }

    /**
     * Visible for admin endpoint tests that need a known reference id.
     */
    public static UUID defaultAdminId() {
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}
