package com.skillseed.wallet.repository;

import com.skillseed.shared.domain.SeedTransactionStatus;
import com.skillseed.wallet.domain.SeedTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeedTransactionRepository extends JpaRepository<SeedTransaction, UUID> {

    Page<SeedTransaction> findByWalletUserId(UUID walletUserId, Pageable pageable);

    Page<SeedTransaction> findByWalletUserIdAndStatus(UUID walletUserId, SeedTransactionStatus status, Pageable pageable);

    List<SeedTransaction> findByWalletUserIdAndStatusAndExpiresAtBefore(
        UUID walletUserId, SeedTransactionStatus status, Instant before);

    List<SeedTransaction> findByBookingId(UUID bookingId);

    boolean existsByWalletUserIdAndDescription(UUID walletUserId, String description);

    Optional<SeedTransaction> findFirstByWalletUserIdOrderByCreatedAtDesc(UUID walletUserId);
}
