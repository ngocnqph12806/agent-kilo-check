package com.skillseed.wallet.repository;

import com.skillseed.shared.domain.SeedTransactionStatus;
import com.skillseed.shared.domain.SeedTransactionType;
import com.skillseed.wallet.domain.SeedTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeedTransactionRepository extends JpaRepository<SeedTransaction, UUID> {

    Page<SeedTransaction> findByWalletUserId(UUID walletUserId, Pageable pageable);

    Page<SeedTransaction> findByWalletUserIdAndStatus(UUID walletUserId, SeedTransactionStatus status, Pageable pageable);

    Page<SeedTransaction> findByWalletUserIdAndTypeIn(
            UUID walletUserId, List<SeedTransactionType> types, Pageable pageable);

    default List<SeedTransaction> findRecentByWalletUserId(UUID walletUserId, Pageable pageable) {
        return findByWalletUserId(
                walletUserId,
                org.springframework.data.domain.PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC, "createdAt")))
                .getContent();
    }

    List<SeedTransaction> findByWalletUserIdAndStatusAndExpiresAtBefore(
        UUID walletUserId, SeedTransactionStatus status, Instant before);

    List<SeedTransaction> findByBookingId(UUID bookingId);

    boolean existsByWalletUserIdAndDescription(UUID walletUserId, String description);

    Optional<SeedTransaction> findFirstByWalletUserIdOrderByCreatedAtDesc(UUID walletUserId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM SeedTransaction t "
            + "WHERE t.wallet.userId = :userId "
            + "AND t.status = com.skillseed.shared.domain.SeedTransactionStatus.COMPLETED "
            + "AND (t.expiresAt IS NULL OR t.expiresAt > :now)")
    int sumActiveBalance(@Param("userId") UUID userId, @Param("now") Instant now);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM SeedTransaction t "
            + "WHERE t.wallet.userId = :userId "
            + "AND t.type = com.skillseed.shared.domain.SeedTransactionType.EXPIRE")
    int sumExpiredAmount(@Param("userId") UUID userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM SeedTransaction t "
            + "WHERE t.wallet.userId = :userId "
            + "AND t.type = com.skillseed.shared.domain.SeedTransactionType.EARN "
            + "AND t.amount > 0 "
            + "AND t.expiresAt IS NOT NULL "
            + "AND t.expiresAt > :now")
    int sumExpiringSoon(@Param("userId") UUID userId, @Param("now") Instant now);

    @Query("SELECT MIN(t.expiresAt) FROM SeedTransaction t "
            + "WHERE t.wallet.userId = :userId "
            + "AND t.type = com.skillseed.shared.domain.SeedTransactionType.EARN "
            + "AND t.amount > 0 "
            + "AND t.expiresAt IS NOT NULL "
            + "AND t.expiresAt > :now")
    Instant findOldestExpiringAt(@Param("userId") UUID userId, @Param("now") Instant now);

    @Query("SELECT t FROM SeedTransaction t WHERE t.status = :status "
            + "AND t.type = com.skillseed.shared.domain.SeedTransactionType.EARN "
            + "AND t.amount > 0 "
            + "AND t.expiresAt IS NOT NULL "
            + "AND t.expiresAt <= :cutoff")
    List<SeedTransaction> findExpiringEarnTx(@Param("status") SeedTransactionStatus status,
                                              @Param("cutoff") Instant cutoff);
}