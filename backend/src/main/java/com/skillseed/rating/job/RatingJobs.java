package com.skillseed.rating.job;

import com.skillseed.rating.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Auto-rate job (T-M173). Backfills 5⭐ ratings on bookings that have
 * been {@code COMPLETED} for more than 7 days without a manual rating
 * from either side. Idempotent — running twice is a no-op.
 */
@Component
public class RatingJobs {

    private static final Logger log = LoggerFactory.getLogger(RatingJobs.class);

    private final RatingService ratingService;

    public RatingJobs(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /** Runs daily at 02:00 UTC. */
    @Scheduled(cron = "0 0 2 * * *")
    public void autoRateStaleBookings() {
        try {
            int count = ratingService.autoRateStaleBookings();
            log.info("[jobs] auto-rated {} stale bookings", count);
        } catch (RuntimeException ex) {
            log.error("[jobs] autoRateStaleBookings failed", ex);
        }
    }
}
