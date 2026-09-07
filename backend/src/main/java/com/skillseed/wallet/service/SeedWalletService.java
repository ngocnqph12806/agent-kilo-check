package com.skillseed.wallet.service;

import com.skillseed.shared.domain.SeedTransactionStatus;
import com.skillseed.shared.domain.SeedTransactionType;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.wallet.domain.SeedTransaction;
import com.skillseed.wallet.domain.SeedWallet;
import com.skillseed.wallet.exception.WalletException;
import com.skillseed.wallet.repository.SeedTransactionRepository;
import com.skillseed.wallet.repository.SeedWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Seed Wallet operations using the ledger pattern (design.md §6).
 *
 * <p>Every balance change goes through a {@link SeedTransaction}
 * row. The {@code balance_cached} column on {@link SeedWallet} is
 * updated atomically inside the same transaction.
 *
 * <p>This is the Phase 1 minimum: only the operations required by
 * onboarding (grant free starter seeds). Earning, spending, refunding
 * and the expiry job are added in the booking / wallet sprints.
 */
@Service
public class SeedWalletService {

    private static final Logger log = LoggerFactory.getLogger(SeedWalletService.class);
    static final int STARTER_SEEDS_AMOUNT = 30;
    static final Duration STARTER_SEEDS_TTL = Duration.ofDays(180);

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

        int balanceAfter = wallet.getBalanceCached() + STARTER_SEEDS_AMOUNT;
        Instant now = Instant.now();
        SeedTransaction tx = new SeedTransaction(
                UUID.randomUUID(),
                wallet,
                SeedTransactionType.GRANT,
                STARTER_SEEDS_AMOUNT,
                balanceAfter);
        tx.setStatus(SeedTransactionStatus.COMPLETED);
        tx.setExpiresAt(now.plus(STARTER_SEEDS_TTL));
        tx.setDescription("Free starter seeds (onboarding)");
        tx.setCreatedAt(now);
        seedTransactionRepository.save(tx);

        wallet.setBalanceCached(balanceAfter);
        wallet.setTotalEarned(wallet.getTotalEarned() + STARTER_SEEDS_AMOUNT);
        wallet.setUpdatedAt(now);
        if (wallet.getLastExpiringAt() == null
                || tx.getExpiresAt().isBefore(wallet.getLastExpiringAt())) {
            wallet.setLastExpiringAt(tx.getExpiresAt());
        }
        seedWalletRepository.save(wallet);
        log.info("Granted {} starter seeds to user id={}, balance={}",
                STARTER_SEEDS_AMOUNT, userId, balanceAfter);
        return tx;
    }

    private SeedWallet createWallet(User user) {
        SeedWallet wallet = new SeedWallet(user);
        wallet.setBalanceCached(0);
        wallet.setTotalEarned(0);
        wallet.setTotalSpent(0);
        wallet.setUpdatedAt(Instant.now());
        return seedWalletRepository.save(wallet);
    }
}