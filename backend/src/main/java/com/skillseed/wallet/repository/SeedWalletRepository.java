package com.skillseed.wallet.repository;

import com.skillseed.wallet.domain.SeedWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeedWalletRepository extends JpaRepository<SeedWallet, UUID> {

    Optional<SeedWallet> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
