package com.skillseed.rating.job;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.rating.service.RatingService;
import com.skillseed.shared.domain.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Daily 01:00 UTC job that auto-rates sessions still missing a
 * learner→teacher review after the grace window.
 *
 * <p>Configuration:
 * <ul>
 *   <li>{@code rating.auto-rate.grace-days} (default 7) — how many
 *       days a booking can stay in {@link BookingStatus#COMPLETED}
 *       before we substitute a 5⭐ rating on the learner's behalf.</li>
 *   <li>{@code rating.auto-rate.cron} overrides the schedule for tests;
 *       defaults to {@code 0 0 1 * * *} (1 AM UTC daily).</li>
 *   <li>{@code rating.auto-rate.enabled} (default true) — kill switch.</li>
 * </ul>
 */
@Component
public class RatingAutoRateJob {

    private static final Logger log = LoggerFactory.getLogger(RatingAutoRateJob.class);

    private final BookingRepository bookingRepository;
    private final RatingService ratingService;
    private final boolean enabled;
    private final Duration grace;

    public RatingAutoRateJob(BookingRepository bookingRepository,
                             RatingService ratingService,
                             @Value("${rating.auto-rate.enabled:true}") boolean enabled,
                             @Value("${rating.auto-rate.grace-days:7}") int graceDays) {
        this.bookingRepository = bookingRepository;
        this.ratingService = ratingService;
        this.enabled = enabled;
        this.grace = Duration.ofDays(Math.max(1, graceDays));
    }

    @Scheduled(cron = "${rating.auto-rate.cron:0 0 1 * * *}", zone = "UTC")
    public void run() {
        if (!enabled) {
            log.debug("Auto-rate job disabled — skipping");
            return;
        }
        Instant cutoff = Instant.now().minus(grace);
        List<Booking> candidates =
                bookingRepository.findByStatusAndUpdatedAtBefore(BookingStatus.COMPLETED, cutoff);
        log.info("Auto-rate job: scanning {} candidate(s) older than {}", candidates.size(), cutoff);
        int inserted = 0;
        for (Booking booking : candidates) {
            try {
                if (ratingService.autoRateIfMissing(booking)) {
                    inserted++;
                }
            } catch (RuntimeException ex) {
                log.error("Auto-rate failed for booking {}", booking.getId(), ex);
            }
        }
        log.info("Auto-rate job done: inserted={} scanned={}", inserted, candidates.size());
    }
}
