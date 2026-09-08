package com.skillseed.wallet.service;

import com.skillseed.booking.domain.Booking;
import com.skillseed.shared.domain.SeedTransactionStatus;
import com.skillseed.shared.domain.SeedTransactionType;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.wallet.domain.SeedTransaction;
import com.skillseed.wallet.domain.SeedWallet;
import com.skillseed.wallet.dto.ExpiringSoonResponse;
import com.skillseed.wallet.dto.SeedTransactionPageResponse;
import com.skillseed.wallet.dto.SeedTransactionResponse;
import com.skillseed.wallet.dto.WalletSummaryResponse;
import com.skillseed.wallet.dto.WalletTier;
import com.skillseed.wallet.exception.WalletException;
import com.skillseed.wallet.repository.SeedTransactionRepository;
import com.skillseed.wallet.repository.SeedWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Seed Wallet operations using the ledger pattern (design.md §6).
 *
 * <p>Every balance change goes through a {@link SeedTransaction}
 * row. The {@code balance_cached} column on {@link SeedWallet} is
 * updated atomically inside the same transaction.
 */
@Service
public class SeedWalletService {

    private static final Logger log = LoggerFactory.getLogger(SeedWalletService.class);
    static final int STARTER_SEEDS_AMOUNT = 30;
    static final Duration STARTER_SEEDS_TTL = Duration.ofDays(180);
    static final Duration EARN_TTL = Duration.ofDays(180);
    static final Duration EXPIRING_SOON_WINDOW = Duration.ofDays(30);

    private final UserRepository userRepository;
    private final SeedWalletRepository seedWalletRepository;
    private final SeedTransactionRepository seedTransactionRepository;

    public SeedWalletService(
            UserRepository userRepository,
            SeedWalletRepository seedWalletRepository,
            SeedTransactionRepository seedTransactionRepository) {
        this.userRepository = userRepository;
        this.seedWalletRepository = seedWalletRepository;
        this.seedTransactionRepository = seedTransactionRepository;
    }

    /**
     * Grants the onboarding starter seeds (amount 30, expires in 6
     * months) to the supplied user. Idempotent: returns the existing
     * starter grant without double-counting if the user already received
     * their starter pack.
     */
    @Transactional
    public SeedTransaction grantStarterSeeds(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> WalletException.notFound("USER_NOT_FOUND",
                        "User not found"));
        SeedWallet wallet = seedWalletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(user));
        if (seedTransactionRepository.existsByWalletUserIdAndDescription(
                userId, "Free starter seeds (onboarding)")) {
            log.info("Starter seeds already granted to user id={}", userId);
            return seedTransactionRepository
                    .findFirstByWalletUserIdOrderByCreatedAtDesc(userId)
                    .orElseThrow(() -> WalletException.badRequest("LEDGER_EMPTY",
                            "Starter grant missing despite existence flag"));
        }

        Instant now = Instant.now();
        return persistGrant(wallet, STARTER_SEEDS_AMOUNT,
                now.plus(STARTER_SEEDS_TTL), "Free starter seeds (onboarding)", now);
    }

    /**
     * Holds {@code amount} seeds for {@code booking} in escrow (status
     * pending). Idempotent: returns the existing escrow row if the same
     * booking has already been debited.
     */
    @Transactional
    public SeedTransaction escrowDebit(Booking booking) {
        UUID userId = booking.getLearner().getId();
        SeedWallet wallet = loadOrCreateWallet(userId);
        if (seedTransactionRepository.findByBookingId(booking.getId()).stream()
                .anyMatch(t -> t.getType() == SeedTransactionType.SPEND)) {
            log.info("Escrow already exists for booking id={}", booking.getId());
            return seedTransactionRepository.findByBookingId(booking.getId()).stream()
                    .filter(t -> t.getType() == SeedTransactionType.SPEND)
                    .findFirst()
                    .orElseThrow(() -> WalletException.badRequest("LEDGER_EMPTY",
                            "Spend row missing despite existence flag"));
        }
        int amount = booking.getSeedAmount();
        if (wallet.getBalanceCached() < amount) {
            throw WalletException.conflict("INSUFFICIENT_BALANCE",
                    "Wallet balance is below the booking seed cost");
        }
        int balanceAfter = wallet.getBalanceCached() - amount;
        Instant now = Instant.now();
        SeedTransaction tx = new SeedTransaction(
                UUID.randomUUID(), wallet, SeedTransactionType.SPEND, -amount, balanceAfter);
        tx.setStatus(SeedTransactionStatus.PENDING);
        tx.setBooking(booking);
        tx.setDescription("Escrow hold for booking " + booking.getId());
        tx.setCreatedAt(now);
        seedTransactionRepository.save(tx);

        wallet.setBalanceCached(balanceAfter);
        wallet.setUpdatedAt(now);
        seedWalletRepository.save(wallet);
        log.info("Escrow {} seeds for booking id={}, balance after={}",
                amount, booking.getId(), balanceAfter);
        return tx;
    }

    /**
     * Releases the escrow: marks the original pending spend row as
     * completed and emits an earn row for the teacher (6 month expiry).
     */
    @Transactional
    public SeedTransaction releaseEscrow(Booking booking) {
        SeedTransaction spend = seedTransactionRepository.findByBookingId(booking.getId()).stream()
                .filter(t -> t.getType() == SeedTransactionType.SPEND)
                .findFirst()
                .orElseThrow(() -> WalletException.badRequest("ESCROW_NOT_FOUND",
                        "No escrow hold for booking " + booking.getId()));
        if (spend.getStatus() == SeedTransactionStatus.COMPLETED) {
            log.info("Escrow already released for booking id={}", booking.getId());
            return spend;
        }
        Instant now = Instant.now();
        spend.setStatus(SeedTransactionStatus.COMPLETED);
        spend.setBalanceAfter(spend.getBalanceAfter());
        seedTransactionRepository.save(spend);

        SeedWallet teacherWallet = loadOrCreateWallet(booking.getTeacher().getId());
        int amount = booking.getSeedAmount();
        Instant expiresAt = now.plus(EARN_TTL);
        SeedTransaction earn = new SeedTransaction(
                UUID.randomUUID(), teacherWallet, SeedTransactionType.EARN,
                amount, teacherWallet.getBalanceCached() + amount);
        earn.setStatus(SeedTransactionStatus.COMPLETED);
        earn.setBooking(booking);
        earn.setDescription("Earned from teaching booking " + booking.getId());
        earn.setExpiresAt(expiresAt);
        earn.setCreatedAt(now);
        seedTransactionRepository.save(earn);

        teacherWallet.setBalanceCached(teacherWallet.getBalanceCached() + amount);
        teacherWallet.setTotalEarned(teacherWallet.getTotalEarned() + amount);
        if (teacherWallet.getLastExpiringAt() == null
                || expiresAt.isBefore(teacherWallet.getLastExpiringAt())) {
            teacherWallet.setLastExpiringAt(expiresAt);
        }
        teacherWallet.setUpdatedAt(now);
        seedWalletRepository.save(teacherWallet);
        log.info("Released escrow booking id={}: teacher {} earned {} seeds",
                booking.getId(), booking.getTeacher().getId(), amount);
        return earn;
    }

    /**
     * Cancels a pending escrow and refunds the learner. Refund percentage
     * is computed by the caller (100% if cancelled ≥24h before the
     * session, 50% otherwise). Negative amounts are clamped to 0.
     */
    @Transactional
    public SeedTransaction refundEscrow(Booking booking, int refundPercent) {
        int amount = booking.getSeedAmount();
        int percent = Math.max(0, Math.min(100, refundPercent));
        // FR-M77: round refund HALF_UP so e.g. 25 seeds × 50% → 13 (not 12).
        // Math.round(double) uses floor(x + 0.5) for positive values, which
        // is half-up to the nearest int — exactly what we need without
        // pulling in BigDecimal arithmetic on the hot path.
        int refundAmount = (int) Math.round((double) amount * percent / 100.0);

        SeedTransaction spend = seedTransactionRepository.findByBookingId(booking.getId()).stream()
                .filter(t -> t.getType() == SeedTransactionType.SPEND)
                .findFirst()
                .orElseThrow(() -> WalletException.badRequest("ESCROW_NOT_FOUND",
                        "No escrow hold for booking " + booking.getId()));
        spend.setStatus(SeedTransactionStatus.CANCELLED);
        seedTransactionRepository.save(spend);

        SeedWallet learnerWallet = loadOrCreateWallet(booking.getLearner().getId());
        Instant now = Instant.now();
        int balanceAfter = learnerWallet.getBalanceCached() + refundAmount;
        SeedTransaction refund = new SeedTransaction(
                UUID.randomUUID(), learnerWallet, SeedTransactionType.REFUND,
                refundAmount, balanceAfter);
        refund.setStatus(SeedTransactionStatus.COMPLETED);
        refund.setBooking(booking);
        refund.setDescription("Refund (" + percent + "%) for cancelled booking " + booking.getId());
        refund.setCreatedAt(now);
        seedTransactionRepository.save(refund);

        learnerWallet.setBalanceCached(balanceAfter);
        if (percent == 100) {
            // Full refund: the original spend never counts against the user.
            learnerWallet.setTotalSpent(Math.max(0, learnerWallet.getTotalSpent() - amount));
        } else if (percent > 0) {
            // Partial refund: only the refunded portion comes off totalSpent;
            // the rest stays as a "kept" spend.
            learnerWallet.setTotalSpent(
                    Math.max(0, learnerWallet.getTotalSpent() - refundAmount));
        }
        learnerWallet.setUpdatedAt(now);
        seedWalletRepository.save(learnerWallet);
        log.info("Refunded {} seeds ({}%) for booking id={}",
                refundAmount, percent, booking.getId());
        return refund;
    }

    /**
     * Cancels a pending escrow with no refund (NO_SHOW or expired
     * booking). The original spend row is flipped to cancelled and the
     * learner spends the seeds.
     */
    @Transactional
    public SeedTransaction forfeitEscrow(Booking booking) {
        SeedTransaction spend = seedTransactionRepository.findByBookingId(booking.getId()).stream()
                .filter(t -> t.getType() == SeedTransactionType.SPEND)
                .findFirst()
                .orElseThrow(() -> WalletException.badRequest("ESCROW_NOT_FOUND",
                        "No escrow hold for booking " + booking.getId()));
        if (spend.getStatus() == SeedTransactionStatus.CANCELLED
                || spend.getStatus() == SeedTransactionStatus.COMPLETED) {
            return spend;
        }
        spend.setStatus(SeedTransactionStatus.CANCELLED);
        seedTransactionRepository.save(spend);

        SeedWallet learnerWallet = loadOrCreateWallet(booking.getLearner().getId());
        learnerWallet.setTotalSpent(learnerWallet.getTotalSpent() + booking.getSeedAmount());
        learnerWallet.setUpdatedAt(Instant.now());
        seedWalletRepository.save(learnerWallet);
        log.info("Forfeited escrow booking id={}", booking.getId());
        return spend;
    }

    /**
     * Snapshot of the wallet's current state for the API. Computed from
     * the ledger with one cached fast-path on {@code balance_cached}.
     */
    @Transactional(readOnly = true)
    public WalletSummaryResponse getWalletSummary(UUID userId) {
        Instant now = Instant.now();
        SeedWallet wallet = seedWalletRepository.findByUserId(userId)
                .orElseThrow(() -> WalletException.notFound("WALLET_NOT_FOUND",
                        "Wallet not provisioned for user"));
        int activeBalance = seedTransactionRepository.sumActiveBalance(userId, now);
        if (activeBalance != wallet.getBalanceCached()) {
            log.warn("balance_cached mismatch for user {}: cached={} computed={}",
                    userId, wallet.getBalanceCached(), activeBalance);
        }
        int totalExpired = seedTransactionRepository.sumExpiredAmount(userId);
        int expiringSoon = seedTransactionRepository.sumExpiringSoon(userId,
                now.plus(EXPIRING_SOON_WINDOW));
        Instant oldestExpiresAt = seedTransactionRepository.findOldestExpiringAt(userId,
                now.plus(EXPIRING_SOON_WINDOW));
        ExpiringSoonResponse expiringResponse = (expiringSoon > 0 && oldestExpiresAt != null)
                ? new ExpiringSoonResponse(expiringSoon, oldestExpiresAt)
                : new ExpiringSoonResponse(0, null);
        return new WalletSummaryResponse(
                wallet.getBalanceCached(),
                wallet.getTotalEarned(),
                wallet.getTotalSpent(),
                totalExpired,
                expiringResponse,
                WalletTier.fromNetEarned(wallet.getTotalEarned()).getDbValue());
    }

    @Transactional(readOnly = true)
    public SeedTransactionPageResponse listTransactions(UUID userId, int page, int size,
                                                        List<SeedTransactionType> types) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? 20 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SeedTransaction> result = (types == null || types.isEmpty())
                ? seedTransactionRepository.findByWalletUserId(userId, pageable)
                : seedTransactionRepository.findByWalletUserIdAndTypeIn(userId, types, pageable);
        return new SeedTransactionPageResponse(
                result.getContent().stream().map(SeedTransactionResponse::from).toList(),
                safePage,
                safeSize,
                result.getTotalElements(),
                result.getTotalPages());
    }

    /**
     * Daily expiry sweep: for every completed EARN transaction whose
     * {@code expiresAt} has passed, emit a matching EXPIRE row and bump
     * the wallet balance accordingly. Returns the number of expired
     * transactions for logging.
     */
    @Transactional
    public int processExpiry() {
        Instant now = Instant.now();
        var due = seedTransactionRepository.findExpiringEarnTx(
                SeedTransactionStatus.COMPLETED, now);
        if (due.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (SeedTransaction original : due) {
            SeedWallet wallet = original.getWallet();
            int balanceAfter = Math.max(0, wallet.getBalanceCached() - original.getAmount());
            SeedTransaction expire = new SeedTransaction(
                    UUID.randomUUID(), wallet, SeedTransactionType.EXPIRE,
                    -original.getAmount(), balanceAfter);
            expire.setStatus(SeedTransactionStatus.COMPLETED);
            expire.setBooking(original.getBooking());
            expire.setDescription("Expired seed from tx " + original.getId());
            expire.setCreatedAt(now);
            seedTransactionRepository.save(expire);

            wallet.setBalanceCached(balanceAfter);
            wallet.setUpdatedAt(now);
            if (wallet.getLastExpiringAt() == null
                    || wallet.getLastExpiringAt().isBefore(now)) {
                wallet.setLastExpiringAt(null);
            }
            seedWalletRepository.save(wallet);
            count++;
        }
        log.info("Expired {} seed transactions", count);
        return count;
    }

    SeedWallet loadOrCreateWallet(UUID userId) {
        return seedWalletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> WalletException.notFound("USER_NOT_FOUND",
                                    "User not found"));
                    return createWallet(user);
                });
    }

    private SeedWallet createWallet(User user) {
        SeedWallet wallet = new SeedWallet(user);
        wallet.setBalanceCached(0);
        wallet.setTotalEarned(0);
        wallet.setTotalSpent(0);
        wallet.setUpdatedAt(Instant.now());
        return seedWalletRepository.save(wallet);
    }

    private SeedTransaction persistGrant(SeedWallet wallet, int amount, Instant expiresAt,
                                          String description, Instant now) {
        int balanceAfter = wallet.getBalanceCached() + amount;
        SeedTransaction tx = new SeedTransaction(
                UUID.randomUUID(),
                wallet,
                SeedTransactionType.GRANT,
                amount,
                balanceAfter);
        tx.setStatus(SeedTransactionStatus.COMPLETED);
        tx.setExpiresAt(expiresAt);
        tx.setDescription(description);
        tx.setCreatedAt(now);
        seedTransactionRepository.save(tx);

        wallet.setBalanceCached(balanceAfter);
        wallet.setTotalEarned(wallet.getTotalEarned() + amount);
        wallet.setUpdatedAt(now);
        if (wallet.getLastExpiringAt() == null
                || expiresAt.isBefore(wallet.getLastExpiringAt())) {
            wallet.setLastExpiringAt(expiresAt);
        }
        seedWalletRepository.save(wallet);
        log.info("Granted {} seeds to user id={}, balance={}",
                amount, wallet.getUserId(), balanceAfter);
        return tx;
    }
}