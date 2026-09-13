package com.skillseed.admin;

import com.skillseed.booking.domain.Booking;
import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.notification.domain.Notification;
import com.skillseed.notification.domain.NotificationRepository;
import com.skillseed.notification.domain.NotificationType;
import com.skillseed.rating.domain.Rating;
import com.skillseed.rating.repository.RatingRepository;
import com.skillseed.session.domain.SessionIncident;
import com.skillseed.session.repository.SessionIncidentRepository;
import com.skillseed.shared.domain.AuthProvider;
import com.skillseed.shared.domain.BookingStatus;
import com.skillseed.shared.domain.Role;
import com.skillseed.shared.domain.SeedTransactionStatus;
import com.skillseed.shared.domain.SeedTransactionType;
import com.skillseed.shared.domain.SkillCategory;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.repository.SkillRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserAvailability;
import com.skillseed.user.domain.UserSkillOffered;
import com.skillseed.user.domain.UserSkillWanted;
import com.skillseed.user.domain.UserWallet;
import com.skillseed.user.repository.UserAvailabilityRepository;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.user.repository.UserSkillOfferedRepository;
import com.skillseed.user.repository.UserSkillWantedRepository;
import com.skillseed.user.repository.UserWalletRepository;
import com.skillseed.waitlist.domain.WaitlistEntry;
import com.skillseed.waitlist.repository.WaitlistRepository;
import com.skillseed.wallet.domain.SeedTransaction;
import com.skillseed.wallet.domain.SeedWallet;
import com.skillseed.wallet.repository.SeedTransactionRepository;
import com.skillseed.wallet.repository.SeedWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Seeds the database with realistic demo data for local development and QA.
 *
 * <p>Scope (default volume):
 * <ul>
 *   <li>25 demo users (varied countries / languages / timezones)</li>
 *   <li>User availability, offered & wanted skills (sampled from V2 seed)</li>
 *   <li>Web3 wallets for half the users</li>
 *   <li>Seed wallets with 30 starter seeds + GRANT transaction</li>
 *   <li>80 bookings spread across all FSM states (PENDING → COMPLETED)</li>
 *   <li>Ratings on completed bookings (learner → teacher)</li>
 *   <li>Notifications (WELCOME + per-booking events)</li>
 *   <li>A handful of session incidents + landing-page waitlist entries</li>
 * </ul>
 *
 * <p>Profile-gated to {@code dev} / {@code local} so it never runs against
 * staging or production. Idempotent via a marker email
 * {@value #MARKER_EMAIL}: re-running on a populated DB is a no-op.
 *
 * <p>Disable with {@code SEED_DEMO_DATA=false} in the environment.
 */
@Component
@Profile({"dev", "local"})
@Order(20)
public class DemoDataSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeedRunner.class);

    static final String MARKER_EMAIL = "__demo_seed_marker__@demo.skillseed";
    private static final String DEMO_PASSWORD = "Demo!2026";
    private static final String DEMO_EMAIL_DOMAIN = "@demo.skillseed";
    private static final int USER_COUNT = 25;
    private static final int BOOKING_COUNT = 80;
    private static final long RNG_SEED = 42L;

    private final UserRepository userRepository;
    private final UserAvailabilityRepository userAvailabilityRepository;
    private final UserSkillOfferedRepository userSkillOfferedRepository;
    private final UserSkillWantedRepository userSkillWantedRepository;
    private final UserWalletRepository userWalletRepository;
    private final SeedWalletRepository seedWalletRepository;
    private final SeedTransactionRepository seedTransactionRepository;
    private final BookingRepository bookingRepository;
    private final RatingRepository ratingRepository;
    private final NotificationRepository notificationRepository;
    private final SessionIncidentRepository sessionIncidentRepository;
    private final WaitlistRepository waitlistRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeedRunner(
            UserRepository userRepository,
            UserAvailabilityRepository userAvailabilityRepository,
            UserSkillOfferedRepository userSkillOfferedRepository,
            UserSkillWantedRepository userSkillWantedRepository,
            UserWalletRepository userWalletRepository,
            SeedWalletRepository seedWalletRepository,
            SeedTransactionRepository seedTransactionRepository,
            BookingRepository bookingRepository,
            RatingRepository ratingRepository,
            NotificationRepository notificationRepository,
            SessionIncidentRepository sessionIncidentRepository,
            WaitlistRepository waitlistRepository,
            SkillRepository skillRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAvailabilityRepository = userAvailabilityRepository;
        this.userSkillOfferedRepository = userSkillOfferedRepository;
        this.userSkillWantedRepository = userSkillWantedRepository;
        this.userWalletRepository = userWalletRepository;
        this.seedWalletRepository = seedWalletRepository;
        this.seedTransactionRepository = seedTransactionRepository;
        this.bookingRepository = bookingRepository;
        this.ratingRepository = ratingRepository;
        this.notificationRepository = notificationRepository;
        this.sessionIncidentRepository = sessionIncidentRepository;
        this.waitlistRepository = waitlistRepository;
        this.skillRepository = skillRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail(MARKER_EMAIL)) {
            log.info("Demo data already seeded (marker {} present) — skipping.", MARKER_EMAIL);
            return;
        }
        long started = System.currentTimeMillis();
        Random rng = new Random(RNG_SEED);

        Map<SkillCategory, List<Skill>> skillsByCategory = loadSkillsByCategory();
        if (skillsByCategory.isEmpty()) {
            log.warn("No skills found in DB — V2 seed migration may not have run. Aborting demo seed.");
            return;
        }

        List<User> users = seedUsers(rng);
        seedAvailability(users, rng);
        seedOfferedSkills(users, skillsByCategory, rng);
        seedWantedSkills(users, skillsByCategory, rng);
        seedUserWallets(users, rng);
        List<SeedWallet> wallets = seedSeedWallets(users);
        seedBookings(users, skillsByCategory, wallets, rng);
        seedWaitlistEntries(rng);
        seedSessionIncidents(rng);

        User marker = new User(UUID.randomUUID(), MARKER_EMAIL, "Demo Seed Marker");
        marker.setVerified(true);
        marker.setRole(Role.ADMIN);
        marker.setAuthProvider(AuthProvider.EMAIL);
        userRepository.save(marker);

        log.info("Demo data seeded: {} users, {} bookings in {} ms. Login with any @demo.skillseed email + password '{}'.",
                users.size(), bookingRepository.count(), System.currentTimeMillis() - started, DEMO_PASSWORD);
    }

    private Map<SkillCategory, List<Skill>> loadSkillsByCategory() {
        Map<SkillCategory, List<Skill>> map = new HashMap<>();
        for (Skill s : skillRepository.findAll()) {
            map.computeIfAbsent(s.getCategory(), k -> new ArrayList<>()).add(s);
        }
        return map;
    }

    private List<User> seedUsers(Random rng) {
        String passwordHash = passwordEncoder.encode(DEMO_PASSWORD);
        List<User> result = new ArrayList<>(USER_COUNT);
        for (int i = 0; i < USER_COUNT; i++) {
            DemoProfile p = DEMO_PROFILES[i];
            // DemoProfile.email already includes the domain suffix (DEMO_EMAIL_DOMAIN).
            // Strip it to isolate the local part, then reattach with a per-user index
            // so each row gets a unique address while keeping the seed printable.
            String localPart = p.email().replace(' ', '-').toLowerCase(Locale.ROOT);
            int atIdx = localPart.indexOf('@');
            if (atIdx > 0) {
                localPart = localPart.substring(0, atIdx);
            }
            String email = localPart + i + DEMO_EMAIL_DOMAIN;
            User u = new User(UUID.randomUUID(), email, p.fullName);
            u.setCountryCode(p.country);
            u.setTimezone(p.timezone);
            u.setLanguages(p.languages);
            u.setBio(p.bio);
            u.setAvatarUrl("https://i.pravatar.cc/300?u=" + u.getId());
            u.setPasswordHash(passwordHash);
            u.setAuthProvider(AuthProvider.EMAIL);
            u.setRole(Role.USER);
            u.setVerified(true);
            u.setVerificationLevel((short) 2);
            u.setOnboardingCompleted(true);
            u.setRatingAvg(BigDecimal.ZERO);
            u.setSessionsCompleted(0);
            result.add(userRepository.save(u));
        }
        return result;
    }

    private void seedAvailability(List<User> users, Random rng) {
        Instant now = Instant.now();
        for (User u : users) {
            int slots = 3 + rng.nextInt(4);
            for (int s = 0; s < slots; s++) {
                short dow = (short) rng.nextInt(7);
                LocalTime start = LocalTime.of(9 + rng.nextInt(8), rng.nextBoolean() ? 0 : 30);
                LocalTime end = start.plusHours(2);
                UserAvailability availability = new UserAvailability(
                        UUID.randomUUID(), u, dow, start, end, u.getTimezone());
                availability.setCreatedAt(now);
                userAvailabilityRepository.save(availability);
            }
        }
    }

    private void seedOfferedSkills(List<User> users, Map<SkillCategory, List<Skill>> skillsByCategory, Random rng) {
        Instant now = Instant.now();
        for (User u : users) {
            int count = 3 + rng.nextInt(3);
            List<SkillCategory> categories = new ArrayList<>(skillsByCategory.keySet());
            for (int i = 0; i < count; i++) {
                SkillCategory cat = categories.get(rng.nextInt(categories.size()));
                Skill skill = pickRandom(skillsByCategory.get(cat), rng);
                if (skill == null) {
                    continue;
                }
                UserSkillOffered offered = new UserSkillOffered(
                        UUID.randomUUID(), u, skill, (short) (3 + rng.nextInt(3)));
                offered.setYearsExperience(2 + rng.nextInt(10));
                offered.setHourlySeedRate(40 + rng.nextInt(80));
                offered.setActive(true);
                offered.setCreatedAt(now);
                offered.setUpdatedAt(now);
                userSkillOfferedRepository.save(offered);
            }
        }
    }

    private void seedWantedSkills(List<User> users, Map<SkillCategory, List<Skill>> skillsByCategory, Random rng) {
        Instant now = Instant.now();
        for (User u : users) {
            int count = 2 + rng.nextInt(3);
            List<SkillCategory> categories = new ArrayList<>(skillsByCategory.keySet());
            for (int i = 0; i < count; i++) {
                SkillCategory cat = categories.get(rng.nextInt(categories.size()));
                Skill skill = pickRandom(skillsByCategory.get(cat), rng);
                if (skill == null) {
                    continue;
                }
                UserSkillWanted wanted = new UserSkillWanted(UUID.randomUUID(), u, skill);
                wanted.setPriority((short) (1 + rng.nextInt(5)));
                wanted.setTargetLevel((short) (2 + rng.nextInt(4)));
                wanted.setCreatedAt(now);
                userSkillWantedRepository.save(wanted);
            }
        }
    }

    private void seedUserWallets(List<User> users, Random rng) {
        long chainId = 11155111L;
        for (int i = 0; i < users.size(); i++) {
            if (i % 2 != 0) {
                continue;
            }
            User u = users.get(i);
            String hex = String.format(Locale.ROOT, "%040x", Math.abs(u.getId().getLeastSignificantBits()));
            String address = "0x" + hex;
            UserWallet wallet = new UserWallet(u, address, chainId);
            wallet.setPrimary(true);
            wallet.setEnsName(u.getFullName().toLowerCase(Locale.ROOT).replace(' ', '-') + ".eth");
            userWalletRepository.save(wallet);
        }
    }

    private List<SeedWallet> seedSeedWallets(List<User> users) {
        List<SeedWallet> wallets = new ArrayList<>();
        Instant now = Instant.now();
        for (User u : users) {
            SeedWallet wallet = new SeedWallet();
            wallet.setUser(u);
            wallet.setBalanceCached(30);
            wallet.setTotalEarned(30);
            wallet.setTotalSpent(0);
            wallet.setLastExpiringAt(now.plus(180, ChronoUnit.DAYS));
            wallet.setUpdatedAt(now);
            SeedWallet saved = seedWalletRepository.save(wallet);
            wallets.add(saved);

            SeedTransaction grant = new SeedTransaction(
                    UUID.randomUUID(), saved, SeedTransactionType.GRANT, 30, 30);
            grant.setStatus(SeedTransactionStatus.COMPLETED);
            grant.setDescription("Starter seeds — welcome to SkillSeed");
            grant.setExpiresAt(now.plus(180, ChronoUnit.DAYS));
            grant.setCreatedAt(now.minus(1, ChronoUnit.DAYS));
            seedTransactionRepository.save(grant);
        }
        return wallets;
    }

    private void seedBookings(List<User> users,
                              Map<SkillCategory, List<Skill>> skillsByCategory,
                              List<SeedWallet> wallets,
                              Random rng) {
        Instant now = Instant.now();
        List<Booking> completedBookings = new ArrayList<>();
        List<BookingStatus> terminalStatuses = List.of(
                BookingStatus.PENDING,
                BookingStatus.PENDING,
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED,
                BookingStatus.CONFIRMED,
                BookingStatus.CONFIRMED,
                BookingStatus.IN_PROGRESS,
                BookingStatus.IN_PROGRESS,
                BookingStatus.COMPLETED,
                BookingStatus.COMPLETED,
                BookingStatus.COMPLETED,
                BookingStatus.COMPLETED,
                BookingStatus.CANCELLED,
                BookingStatus.DECLINED);
        short[] durations = {15, 30, 30, 45, 60, 60};

        for (int i = 0; i < BOOKING_COUNT; i++) {
            BookingStatus status = terminalStatuses.get(rng.nextInt(terminalStatuses.size()));
            User teacher = users.get(rng.nextInt(users.size()));
            User learner = pickOther(teacher, users, rng);
            Skill skill = pickRandom(allSkills(skillsByCategory), rng);
            short duration = durations[rng.nextInt(durations.length)];
            int seedAmount = seedAmountFor(duration);
            Instant scheduled = scheduledFor(status, now, rng);
            Booking booking = new Booking(
                    UUID.randomUUID(), teacher, learner, skill, scheduled, duration, seedAmount);
            booking.setStatus(status);
            booking.setCreatedAt(scheduled.minus(2, ChronoUnit.DAYS));
            booking.setUpdatedAt(scheduled);
            if (status == BookingStatus.CANCELLED || status == BookingStatus.DECLINED) {
                booking.setCancelledBy(rng.nextBoolean() ? learner : teacher);
                booking.setCancellationReason("schedule_conflict");
                booking.setUpdatedAt(scheduled.minus(1, ChronoUnit.HOURS));
            }
            bookingRepository.save(booking);

            SeedWallet learnerWallet = wallets.stream()
                    .filter(w -> w.getUser().getId().equals(learner.getId()))
                    .findFirst().orElse(null);
            SeedWallet teacherWallet = wallets.stream()
                    .filter(w -> w.getUser().getId().equals(teacher.getId()))
                    .findFirst().orElse(null);

            if (learnerWallet != null && status != BookingStatus.PENDING) {
                SeedTransaction spend = new SeedTransaction(
                        UUID.randomUUID(), learnerWallet, SeedTransactionType.SPEND,
                        -seedAmount, Math.max(0, learnerWallet.getBalanceCached() - seedAmount));
                spend.setBooking(booking);
                spend.setStatus(SeedTransactionStatus.COMPLETED);
                spend.setDescription("Escrow hold for booking with " + teacher.getFullName());
                spend.setCreatedAt(scheduled.minus(2, ChronoUnit.DAYS));
                seedTransactionRepository.save(spend);
                learnerWallet.setBalanceCached(Math.max(0, learnerWallet.getBalanceCached() - seedAmount));
                learnerWallet.setTotalSpent(learnerWallet.getTotalSpent() + seedAmount);
                learnerWallet.setUpdatedAt(scheduled.minus(2, ChronoUnit.DAYS));
                seedWalletRepository.save(learnerWallet);
            }

            if (status == BookingStatus.COMPLETED && teacherWallet != null) {
                SeedTransaction earn = new SeedTransaction(
                        UUID.randomUUID(), teacherWallet, SeedTransactionType.EARN,
                        seedAmount, teacherWallet.getBalanceCached() + seedAmount);
                earn.setBooking(booking);
                earn.setStatus(SeedTransactionStatus.COMPLETED);
                earn.setDescription("Session completed — payout for " + learner.getFullName());
                earn.setExpiresAt(scheduled.plus(180, ChronoUnit.DAYS));
                earn.setCreatedAt(scheduled.plus(1, ChronoUnit.HOURS));
                seedTransactionRepository.save(earn);
                teacherWallet.setBalanceCached(teacherWallet.getBalanceCached() + seedAmount);
                teacherWallet.setTotalEarned(teacherWallet.getTotalEarned() + seedAmount);
                teacherWallet.setUpdatedAt(scheduled.plus(1, ChronoUnit.HOURS));
                seedWalletRepository.save(teacherWallet);
                completedBookings.add(booking);
            }

            if (status == BookingStatus.CANCELLED || status == BookingStatus.DECLINED) {
                if (learnerWallet != null) {
                    SeedTransaction refund = new SeedTransaction(
                            UUID.randomUUID(), learnerWallet, SeedTransactionType.REFUND,
                            seedAmount, learnerWallet.getBalanceCached());
                    refund.setBooking(booking);
                    refund.setStatus(SeedTransactionStatus.COMPLETED);
                    refund.setDescription("Refund — booking " + status.name().toLowerCase(Locale.ROOT));
                    refund.setCreatedAt(scheduled.minus(1, ChronoUnit.HOURS));
                    seedTransactionRepository.save(refund);
                    learnerWallet.setBalanceCached(learnerWallet.getBalanceCached() + seedAmount);
                    learnerWallet.setUpdatedAt(scheduled.minus(1, ChronoUnit.HOURS));
                    seedWalletRepository.save(learnerWallet);
                }
            }

            notifyLearner(learner, booking, status, rng);
            notifyTeacher(teacher, booking, status, rng);
        }

        seedRatings(completedBookings, rng);
    }

    private void seedRatings(List<Booking> completed, Random rng) {
        String[] positives = {
                "Great session, very clear explanations.",
                "Patient and knowledgeable — learned a lot.",
                "Exactly what I needed. Highly recommend.",
                "Helpful examples and good pacing.",
                "Came prepared with great materials."
        };
        for (Booking b : completed) {
            short score = (short) (4 + rng.nextInt(2));
            Rating rating = new Rating(UUID.randomUUID(), b, b.getLearner(), b.getTeacher());
            rating.setOverallScore(score);
            rating.setHelpfulnessScore((short) (4 + rng.nextInt(2)));
            rating.setRespectfulnessScore((short) (4 + rng.nextInt(2)));
            rating.setKnowledgeScore((short) (4 + rng.nextInt(2)));
            rating.setClarityScore((short) (4 + rng.nextInt(2)));
            rating.setPunctualityScore((short) (4 + rng.nextInt(2)));
            rating.setFriendlinessScore((short) (4 + rng.nextInt(2)));
            rating.setWouldRecommend(score >= 4);
            rating.setReviewText(positives[rng.nextInt(positives.length)]);
            rating.setAutoRated(false);
            rating.setCreatedAt(b.getScheduledAt().plus(90, ChronoUnit.MINUTES));
            ratingRepository.save(rating);
        }
    }

    private void notifyLearner(User learner, Booking b, BookingStatus status, Random rng) {
        NotificationType type = switch (status) {
            case PENDING -> NotificationType.BOOKING_REQUEST;
            case CONFIRMED, IN_PROGRESS -> NotificationType.BOOKING_ACCEPTED;
            case COMPLETED -> rng.nextBoolean() ? NotificationType.SESSION_COMPLETED : NotificationType.RATING_PROMPT;
            case CANCELLED -> NotificationType.BOOKING_CANCELLED;
            case DECLINED -> NotificationType.BOOKING_DECLINED;
            default -> NotificationType.SYSTEM;
        };
        Map<String, Object> payload = Map.of(
                "bookingId", b.getId().toString(),
                "teacherName", b.getTeacher().getFullName(),
                "skillName", b.getSkill().getName(),
                "scheduledAt", b.getScheduledAt().toString());
        Notification n = new Notification(UUID.randomUUID(), learner, type, payload);
        notificationRepository.save(n);
    }

    private void notifyTeacher(User teacher, Booking b, BookingStatus status, Random rng) {
        NotificationType type = switch (status) {
            case PENDING -> NotificationType.BOOKING_REQUEST;
            case CONFIRMED -> NotificationType.BOOKING_ACCEPTED;
            case COMPLETED -> NotificationType.SESSION_COMPLETED;
            case CANCELLED, DECLINED -> NotificationType.BOOKING_CANCELLED;
            default -> NotificationType.SYSTEM;
        };
        Map<String, Object> payload = Map.of(
                "bookingId", b.getId().toString(),
                "learnerName", b.getLearner().getFullName(),
                "skillName", b.getSkill().getName(),
                "scheduledAt", b.getScheduledAt().toString());
        Notification n = new Notification(UUID.randomUUID(), teacher, type, payload);
        if (rng.nextDouble() < 0.4) {
            n.markRead(b.getScheduledAt().minus(1, ChronoUnit.HOURS));
        }
        notificationRepository.save(n);
    }

    private void seedWaitlistEntries(Random rng) {
        String[] sources = {"landing", "twitter", "producthunt", "friend"};
        String[] referrers = {"https://skillseed.io", "https://twitter.com/skillseed", "https://news.ycombinator.com"};
        String[] agents = {
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15"};
        String[] ips = {"203.0.113.42", "198.51.100.7", "192.0.2.99"};
        for (int i = 0; i < 8; i++) {
            String email = "waitlist" + i + DEMO_EMAIL_DOMAIN;
            if (waitlistRepository.existsByEmailLower(email)) {
                continue;
            }
            WaitlistEntry entry = new WaitlistEntry(
                    email,
                    sources[rng.nextInt(sources.length)],
                    referrers[rng.nextInt(referrers.length)],
                    agents[rng.nextInt(agents.length)],
                    ips[rng.nextInt(ips.length)]);
            if (rng.nextBoolean()) {
                entry.markConfirmed();
            }
            waitlistRepository.save(entry);
        }
    }

    private void seedSessionIncidents(Random rng) {
        String[] categories = {"audio_quality", "video_quality", "no_show", "inappropriate_behavior", "technical_issue"};
        String[] descriptions = {
                "Microphone kept cutting out for the first 10 minutes.",
                "Learner did not join the call after 15 minutes.",
                "Frequent disconnections on the teacher's side.",
                "Inappropriate language in chat — flagged for review.",
                "Screen share stopped working mid-session."
        };
        List<Booking> recent = bookingRepository.findAll(PageRequest.of(0, 10)).getContent();
        for (int i = 0; i < Math.min(4, recent.size()); i++) {
            Booking b = recent.get(i);
            SessionIncident incident = new SessionIncident(
                    UUID.randomUUID(),
                    b.getId(),
                    b.getLearner().getId(),
                    categories[rng.nextInt(categories.length)],
                    descriptions[rng.nextInt(descriptions.length)],
                    b.getScheduledAt().plus(30, ChronoUnit.MINUTES));
            incident.setStatus(rng.nextBoolean() ? "open" : "investigating");
            sessionIncidentRepository.save(incident);
        }
    }

    private static Skill pickRandom(List<Skill> pool, Random rng) {
        return pool == null || pool.isEmpty() ? null : pool.get(rng.nextInt(pool.size()));
    }

    private static User pickOther(User exclude, List<User> users, Random rng) {
        for (int attempts = 0; attempts < 8; attempts++) {
            User candidate = users.get(rng.nextInt(users.size()));
            if (!candidate.getId().equals(exclude.getId())) {
                return candidate;
            }
        }
        return users.stream().filter(u -> !u.getId().equals(exclude.getId())).findFirst().orElse(exclude);
    }

    private static List<Skill> allSkills(Map<SkillCategory, List<Skill>> byCategory) {
        List<Skill> all = new ArrayList<>();
        byCategory.values().forEach(all::addAll);
        return all;
    }

    private static Instant scheduledFor(BookingStatus status, Instant now, Random rng) {
        return switch (status) {
            case PENDING, CONFIRMED -> now.plus(1 + rng.nextInt(20), ChronoUnit.DAYS)
                    .plus(rng.nextInt(24), ChronoUnit.HOURS);
            case IN_PROGRESS -> now.minus(rng.nextInt(60), ChronoUnit.MINUTES);
            case COMPLETED -> now.minus(1 + rng.nextInt(60), ChronoUnit.DAYS)
                    .plus(rng.nextInt(24), ChronoUnit.HOURS);
            case CANCELLED, DECLINED -> now.minus(1 + rng.nextInt(45), ChronoUnit.DAYS);
            default -> now;
        };
    }

    private static int seedAmountFor(short durationMinutes) {
        return switch (durationMinutes) {
            case 15, 30 -> 1;
            case 45 -> 2;
            case 60 -> 3;
            case 90 -> 4;
            default -> 1;
        };
    }

    private record DemoProfile(String fullName, String email, String country, String timezone,
                               String[] languages, String bio) {
    }

    private static final DemoProfile[] DEMO_PROFILES = new DemoProfile[]{
            new DemoProfile("Nguyễn Minh Anh", "minh-anh" + DEMO_EMAIL_DOMAIN, "VN", "Asia/Ho_Chi_Minh",
                    new String[]{"vi", "en"},
                    "Senior backend engineer, mentor for 5+ years. Loves systems design interviews."),
            new DemoProfile("Trần Quốc Bảo", "quoc-bao" + DEMO_EMAIL_DOMAIN, "VN", "Asia/Ho_Chi_Minh",
                    new String[]{"vi", "en"},
                    "Product designer focused on accessibility. Available weekday evenings."),
            new DemoProfile("Lê Hồng Phương", "hong-phuong" + DEMO_EMAIL_DOMAIN, "VN", "Asia/Ho_Chi_Minh",
                    new String[]{"vi", "en"},
                    "Data scientist at a fintech. Happy to talk SQL, Python, and interview prep."),
            new DemoProfile("Phạm Đức Thịnh", "duc-thinh" + DEMO_EMAIL_DOMAIN, "VN", "Asia/Ho_Chi_Minh",
                    new String[]{"vi", "en"},
                    "iOS engineer who also teaches guitar on weekends."),
            new DemoProfile("Sara Mitchell", "sara.mitchell" + DEMO_EMAIL_DOMAIN, "US", "America/Los_Angeles",
                    new String[]{"en"},
                    "Technical writer and ex-Googler. Coaching on resume + portfolio."),
            new DemoProfile("James O'Connor", "james.oconnor" + DEMO_EMAIL_DOMAIN, "IE", "Europe/Dublin",
                    new String[]{"en"},
                    "Staff engineer at a payments startup. Mock interviews + system design."),
            new DemoProfile("Yuki Tanaka", "yuki.tanaka" + DEMO_EMAIL_DOMAIN, "JP", "Asia/Tokyo",
                    new String[]{"ja", "en"},
                    "Frontend specialist. React, TypeScript, accessibility."),
            new DemoProfile("Mei Lin", "mei.lin" + DEMO_EMAIL_DOMAIN, "SG", "Asia/Singapore",
                    new String[]{"en", "zh"},
                    "PM at a SaaS company. Career coaching for PM interviews."),
            new DemoProfile("Arjun Kapoor", "arjun.kapoor" + DEMO_EMAIL_DOMAIN, "IN", "Asia/Kolkata",
                    new String[]{"en", "hi"},
                    "Cloud architect, AWS & Kubernetes. Loves whiteboard sessions."),
            new DemoProfile("Priya Sharma", "priya.sharma" + DEMO_EMAIL_DOMAIN, "IN", "Asia/Kolkata",
                    new String[]{"en", "hi"},
                    "ML engineer. Python, deep learning, LLM fine-tuning."),
            new DemoProfile("Lucas Müller", "lucas.muller" + DEMO_EMAIL_DOMAIN, "DE", "Europe/Berlin",
                    new String[]{"de", "en"},
                    "Security engineer. Web app pen testing, OWASP."),
            new DemoProfile("Sophie Dubois", "sophie.dubois" + DEMO_EMAIL_DOMAIN, "FR", "Europe/Paris",
                    new String[]{"fr", "en"},
                    "UX researcher. Discovery interviews, usability testing."),
            new DemoProfile("Mateo Silva", "mateo.silva" + DEMO_EMAIL_DOMAIN, "BR", "America/Sao_Paulo",
                    new String[]{"pt", "en"},
                    "Mobile dev, Flutter. Building cross-platform apps since 2017."),
            new DemoProfile("Hyejin Park", "hyejin.park" + DEMO_EMAIL_DOMAIN, "KR", "Asia/Seoul",
                    new String[]{"ko", "en"},
                    "Game developer. Unity, C#, level design."),
            new DemoProfile("Aiden Walker", "aiden.walker" + DEMO_EMAIL_DOMAIN, "AU", "Australia/Sydney",
                    new String[]{"en"},
                    "DevOps lead. CI/CD, observability, on-call sanity."),
            new DemoProfile("Olivia Brown", "olivia.brown" + DEMO_EMAIL_DOMAIN, "GB", "Europe/London",
                    new String[]{"en"},
                    "Marketing strategist. Growth, positioning, B2B SaaS."),
            new DemoProfile("Diego Ramirez", "diego.ramirez" + DEMO_EMAIL_DOMAIN, "MX", "America/Mexico_City",
                    new String[]{"es", "en"},
                    "iOS dev. Swift, SwiftUI, app store optimization."),
            new DemoProfile("Vũ Hà Linh", "ha-linh" + DEMO_EMAIL_DOMAIN, "VN", "Asia/Ho_Chi_Minh",
                    new String[]{"vi", "en"},
                    "Junior dev learning React. Happy to swap skills for English conversation."),
            new DemoProfile("Ngô Khánh Vy", "khanh-vy" + DEMO_EMAIL_DOMAIN, "VN", "Asia/Ho_Chi_Minh",
                    new String[]{"vi", "en"},
                    "Math teacher. Algebra, calculus, test prep."),
            new DemoProfile("Hoàng Tuấn Kiệt", "tuan-kiet" + DEMO_EMAIL_DOMAIN, "VN", "Asia/Ho_Chi_Minh",
                    new String[]{"vi", "en"},
                    "Photographer. Composition, lighting, Lightroom workflow."),
            new DemoProfile("Emily Carter", "emily.carter" + DEMO_EMAIL_DOMAIN, "CA", "America/Toronto",
                    new String[]{"en", "fr"},
                    "Engineering manager. 1:1 coaching, performance reviews."),
            new DemoProfile("Ravi Iyer", "ravi.iyer" + DEMO_EMAIL_DOMAIN, "IN", "Asia/Kolkata",
                    new String[]{"en"},
                    "Backend engineer. Java, Spring Boot, distributed systems."),
            new DemoProfile("Hannah Schmidt", "hannah.schmidt" + DEMO_EMAIL_DOMAIN, "DE", "Europe/Berlin",
                    new String[]{"de", "en"},
                    "Data analyst. SQL, Tableau, business storytelling."),
            new DemoProfile("Kenji Nakamura", "kenji.nakamura" + DEMO_EMAIL_DOMAIN, "JP", "Asia/Tokyo",
                    new String[]{"ja", "en"},
                    "Founder & CTO. Pitching, fundraising, early-stage hiring."),
            new DemoProfile("Isabela Costa", "isabela.costa" + DEMO_EMAIL_DOMAIN, "BR", "America/Sao_Paulo",
                    new String[]{"pt", "en"},
                    "Voice coach. Public speaking, presentation skills, accent work.")
    };

}
