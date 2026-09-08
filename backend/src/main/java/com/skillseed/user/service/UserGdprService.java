package com.skillseed.user.service;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.notification.domain.NotificationRepository;
import com.skillseed.rating.domain.Rating;
import com.skillseed.rating.repository.RatingRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserAvailability;
import com.skillseed.user.domain.UserSkillOffered;
import com.skillseed.user.domain.UserSkillWanted;
import com.skillseed.user.dto.UserDataExportResponse;
import com.skillseed.user.dto.UserDataExportResponse.AvailabilitySection;
import com.skillseed.user.dto.UserDataExportResponse.BookingSection;
import com.skillseed.user.dto.UserDataExportResponse.OfferedSkillSection;
import com.skillseed.user.dto.UserDataExportResponse.ProfileSection;
import com.skillseed.user.dto.UserDataExportResponse.RatingSection;
import com.skillseed.user.dto.UserDataExportResponse.SeedTransactionSection;
import com.skillseed.user.dto.UserDataExportResponse.WalletSection;
import com.skillseed.user.dto.UserDataExportResponse.WantedSkillSection;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserAvailabilityRepository;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.user.repository.UserSkillOfferedRepository;
import com.skillseed.user.repository.UserSkillWantedRepository;
import com.skillseed.wallet.domain.SeedTransaction;
import com.skillseed.wallet.domain.SeedWallet;
import com.skillseed.wallet.repository.SeedTransactionRepository;
import com.skillseed.wallet.repository.SeedWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * GDPR right-to-delete + data export (T-M200, T-M201).
 *
 * <p>Two operations are exposed:
 * <ul>
 *   <li>{@link #softDelete(UUID)} — marks the user as deleted, anonymises
 *       PII fields (email, phone, fullName, bio, avatarUrl) and removes
 *       recurring availability. Bookings, ratings and ledger entries
 *       are preserved for the 30-day cooling-off window so that other
 *       users can still resolve disputes and view historical context.
 *       The user can no longer authenticate.</li>
 *   <li>{@link #export(UUID)} — returns a single JSON document
 *       containing every personal-data field we hold for the user.</li>
 * </ul>
 *
 * <p>A scheduled job {@link com.skillseed.user.job.UserGdprJobs} purges
 * rows whose {@code deletedAt} is older than 30 days.
 */
@Service
public class UserGdprService {

    static final int HARD_DELETE_RETENTION_DAYS = 30;
    private static final int EXPORT_TX_LIMIT = 500;
    private static final int EXPORT_BOOKING_LIMIT = 500;
    private static final int EXPORT_RATING_LIMIT = 500;

    private static final Logger log = LoggerFactory.getLogger(UserGdprService.class);

    private final UserRepository userRepository;
    private final UserSkillOfferedRepository userSkillOfferedRepository;
    private final UserSkillWantedRepository userSkillWantedRepository;
    private final UserAvailabilityRepository userAvailabilityRepository;
    private final SeedWalletRepository seedWalletRepository;
    private final SeedTransactionRepository seedTransactionRepository;
    private final BookingRepository bookingRepository;
    private final RatingRepository ratingRepository;
    private final NotificationRepository notificationRepository;

    public UserGdprService(
            UserRepository userRepository,
            UserSkillOfferedRepository userSkillOfferedRepository,
            UserSkillWantedRepository userSkillWantedRepository,
            UserAvailabilityRepository userAvailabilityRepository,
            SeedWalletRepository seedWalletRepository,
            SeedTransactionRepository seedTransactionRepository,
            BookingRepository bookingRepository,
            RatingRepository ratingRepository,
            NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.userSkillOfferedRepository = userSkillOfferedRepository;
        this.userSkillWantedRepository = userSkillWantedRepository;
        this.userAvailabilityRepository = userAvailabilityRepository;
        this.seedWalletRepository = seedWalletRepository;
        this.seedTransactionRepository = seedTransactionRepository;
        this.bookingRepository = bookingRepository;
        this.ratingRepository = ratingRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Soft-delete the user (T-M200). Idempotent — calling twice on the
     * same id is a no-op.
     */
    @Transactional
    public void softDelete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound("USER_NOT_FOUND", "User not found"));
        if (user.getDeletedAt() != null) {
            log.info("GDPR soft-delete: user id={} already deleted at {}", userId, user.getDeletedAt());
            return;
        }

        Instant now = Instant.now();
        String tombstone = "deleted-" + userId;
        user.setEmail(tombstone + "@deleted.skillseed.local");
        user.setPhone(null);
        user.setPasswordHash(null);
        user.setFullName("Deleted user");
        user.setAvatarUrl(null);
        user.setBio(null);
        user.setLanguages(new String[]{"en"});
        user.setVerified(false);
        user.setVerificationLevel((short) 0);
        user.setOnboardingCompleted(false);
        user.setDeletedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);

        userAvailabilityRepository.deleteByUserId(userId);
        userSkillOfferedRepository.findByUserIdAndActiveTrue(userId)
                .forEach(s -> s.setActive(false));
        userSkillWantedRepository.findByUserId(userId)
                .forEach(userSkillWantedRepository::delete);

        log.info("GDPR soft-delete completed for user id={} (hard-delete after {})",
                userId, HARD_DELETE_RETENTION_DAYS);
    }

    /**
     * Build a JSON snapshot of every personal-data field for the user
     * (T-M201).
     */
    @Transactional(readOnly = true)
    public UserDataExportResponse export(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound("USER_NOT_FOUND", "User not found"));

        ProfileSection profile = new ProfileSection(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getCountryCode(),
                user.getTimezone(),
                user.getLanguages() == null ? List.of() : List.of(user.getLanguages()),
                user.getLearningStyle(),
                user.getAuthProvider().name().toLowerCase(Locale.ROOT),
                user.isVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt());

        List<OfferedSkillSection> offered = userSkillOfferedRepository
                .findByUserIdAndActiveTrue(userId)
                .stream()
                .map(this::toOfferedSection)
                .toList();

        List<WantedSkillSection> wanted = userSkillWantedRepository
                .findByUserId(userId)
                .stream()
                .map(this::toWantedSection)
                .toList();

        List<AvailabilitySection> availability = userAvailabilityRepository
                .findByUserId(userId)
                .stream()
                .map(this::toAvailabilitySection)
                .toList();

        Optional<SeedWallet> wallet = seedWalletRepository.findByUserId(userId);
        WalletSection walletSection = wallet
                .map(w -> new WalletSection(
                        w.getBalanceCached(),
                        w.getTotalEarned(),
                        w.getTotalSpent()))
                .orElseGet(() -> new WalletSection(0, 0, 0));

        List<SeedTransactionSection> transactions = wallet
                .map(w -> seedTransactionRepository
                        .findByWalletUserId(w.getUserId(),
                                PageRequest.of(0, EXPORT_TX_LIMIT))
                        .stream()
                        .map(this::toTxSection)
                        .toList())
                .orElseGet(List::of);

        List<BookingSection> asTeacher = bookingRepository
                .findByTeacherId(userId, PageRequest.of(0, EXPORT_BOOKING_LIMIT))
                .stream()
                .map(b -> toBookingSection(b, userId))
                .toList();

        List<BookingSection> asLearner = bookingRepository
                .findByLearnerId(userId, PageRequest.of(0, EXPORT_BOOKING_LIMIT))
                .stream()
                .map(b -> toBookingSection(b, userId))
                .toList();

        List<RatingSection> given = ratingRepository.findByRaterId(userId).stream()
                .limit(EXPORT_RATING_LIMIT)
                .map(this::toRatingSection)
                .toList();

        List<RatingSection> received = ratingRepository
                .findByRateeId(userId, PageRequest.of(0, EXPORT_RATING_LIMIT))
                .stream()
                .map(this::toRatingSection)
                .toList();

        return new UserDataExportResponse(
                Instant.now(),
                profile,
                offered,
                wanted,
                availability,
                walletSection,
                transactions,
                asTeacher,
                asLearner,
                given,
                received);
    }

    /**
     * Hard-delete users whose soft-delete retention period has elapsed
     * (T-M200). Returns the number of users purged.
     */
    @Transactional
    public int purgeExpiredDeletedUsers() {
        Instant cutoff = Instant.now().minus(HARD_DELETE_RETENTION_DAYS, ChronoUnit.DAYS);
        List<User> victims = userRepository.findAll().stream()
                .filter(u -> u.getDeletedAt() != null && u.getDeletedAt().isBefore(cutoff))
                .toList();
        int count = 0;
        for (User u : victims) {
            UUID id = u.getId();
            notificationRepository.findAll().stream()
                    .filter(n -> n.getUser() != null && id.equals(n.getUser().getId()))
                    .forEach(notificationRepository::delete);
            userRepository.delete(u);
            count++;
        }
        if (count > 0) {
            log.info("GDPR hard-delete purged {} users", count);
        }
        return count;
    }

    private OfferedSkillSection toOfferedSection(UserSkillOffered uso) {
        return new OfferedSkillSection(
                uso.getSkill().getSlug(),
                uso.getSkill().getName(),
                uso.getSkill().getCategory().getDbValue(),
                Short.toString(uso.getLevel()),
                uso.getYearsExperience(),
                uso.getDescription(),
                uso.getHourlySeedRate());
    }

    private WantedSkillSection toWantedSection(UserSkillWanted usw) {
        Short targetLevel = usw.getTargetLevel();
        return new WantedSkillSection(
                usw.getSkill().getSlug(),
                usw.getSkill().getName(),
                usw.getSkill().getCategory().getDbValue(),
                targetLevel == null ? null : Short.toString(targetLevel),
                usw.getNotes());
    }

    private AvailabilitySection toAvailabilitySection(UserAvailability a) {
        return new AvailabilitySection(
                a.getTimezone(),
                a.getDayOfWeek(),
                a.getStartTime().toString(),
                a.getEndTime().toString());
    }

    private SeedTransactionSection toTxSection(SeedTransaction tx) {
        return new SeedTransactionSection(
                tx.getType().name(),
                tx.getAmount(),
                tx.getBalanceAfter(),
                tx.getDescription(),
                tx.getStatus().name(),
                tx.getCreatedAt(),
                tx.getExpiresAt());
    }

    private BookingSection toBookingSection(Booking b, UUID userId) {
        User other = userId.equals(b.getTeacher().getId())
                ? b.getLearner()
                : b.getTeacher();
        return new BookingSection(
                b.getId(),
                other == null ? null : other.getId(),
                other == null ? null : other.getFullName(),
                b.getStatus().name(),
                b.getScheduledAt(),
                b.getDurationMinutes(),
                b.getCreatedAt());
    }

    private RatingSection toRatingSection(Rating r) {
        Short score = r.getOverallScore();
        return new RatingSection(
                r.getBooking() == null ? null : r.getBooking().getId(),
                r.getRater() == null ? null : r.getRater().getId(),
                r.getRatee() == null ? null : r.getRatee().getId(),
                score == null ? null : score.intValue(),
                r.getReviewText(),
                r.isAutoRated(),
                r.getCreatedAt());
    }
}
