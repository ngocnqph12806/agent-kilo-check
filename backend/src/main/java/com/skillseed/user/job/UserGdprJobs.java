package com.skillseed.user.job;

import com.skillseed.user.service.UserGdprService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled GDPR housekeeping (T-M200). Purges soft-deleted users
 * whose {@code deletedAt} is older than the retention window
 * (30 days). Runs daily at 02:30 UTC.
 */
@Component
public class UserGdprJobs {

    private static final Logger log = LoggerFactory.getLogger(UserGdprJobs.class);

    private final UserGdprService userGdprService;

    public UserGdprJobs(UserGdprService userGdprService) {
        this.userGdprService = userGdprService;
    }

    @Scheduled(cron = "0 30 2 * * *")
    public void purgeExpiredDeletedUsers() {
        try {
            int count = userGdprService.purgeExpiredDeletedUsers();
            if (count > 0) {
                log.info("[gdpr-jobs] hard-deleted {} expired soft-deleted users", count);
            }
        } catch (RuntimeException ex) {
            log.error("[gdpr-jobs] purgeExpiredDeletedUsers failed", ex);
        }
    }
}