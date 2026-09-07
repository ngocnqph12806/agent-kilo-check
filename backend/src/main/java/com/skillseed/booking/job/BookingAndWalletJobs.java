package com.skillseed.booking.job;

import com.skillseed.booking.service.BookingService;
import com.skillseed.wallet.service.SeedWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled jobs for the booking and wallet modules (T-M106, T-M107,
 * T-M108, T-M125).
 *
 * <p>All jobs are idempotent — they only act on rows whose state
 * matches the trigger condition.
 */
@Component
public class BookingAndWalletJobs {

    private static final Logger log = LoggerFactory.getLogger(BookingAndWalletJobs.class);

    private final BookingService bookingService;
    private final SeedWalletService seedWalletService;

    public BookingAndWalletJobs(BookingService bookingService, SeedWalletService seedWalletService) {
        this.bookingService = bookingService;
        this.seedWalletService = seedWalletService;
    }

    /** Runs every 5 minutes: expire pending bookings older than 24h. */
    @Scheduled(cron = "0 */5 * * * *")
    public void expirePendingBookings() {
        try {
            int count = bookingService.expirePendingBookings();
            if (count > 0) {
                log.info("[jobs] expired {} pending bookings", count);
            }
        } catch (RuntimeException ex) {
            log.error("[jobs] expirePendingBookings failed", ex);
        }
    }

    /** Runs every minute: mark NO_SHOW if nobody joined within 10 min after scheduledAt. */
    @Scheduled(cron = "0 * * * * *")
    public void markNoShows() {
        try {
            int count = bookingService.markNoShows();
            if (count > 0) {
                log.info("[jobs] marked {} bookings as NO_SHOW", count);
            }
        } catch (RuntimeException ex) {
            log.error("[jobs] markNoShows failed", ex);
        }
    }

    /** Runs every 5 minutes: send 24h + 1h reminders for upcoming confirmed sessions. */
    @Scheduled(cron = "0 */5 * * * *")
    public void sendReminders() {
        try {
            int count = bookingService.sendReminders();
            if (count > 0) {
                log.info("[jobs] sent {} reminders", count);
            }
        } catch (RuntimeException ex) {
            log.error("[jobs] sendReminders failed", ex);
        }
    }

    /** Runs daily at 01:00 UTC: process seed expiry (T-M125). */
    @Scheduled(cron = "0 0 1 * * *")
    public void processSeedExpiry() {
        try {
            int count = seedWalletService.processExpiry();
            log.info("[jobs] expired {} seed transactions", count);
        } catch (RuntimeException ex) {
            log.error("[jobs] processSeedExpiry failed", ex);
        }
    }
}