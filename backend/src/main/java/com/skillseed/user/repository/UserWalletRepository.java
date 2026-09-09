package com.skillseed.user.repository;

import com.skillseed.user.domain.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, UUID> {

    Optional<UserWallet> findByAddressLower(String addressLower);

    List<UserWallet> findByUserId(UUID userId);

    boolean existsByAddressLower(String addressLower);
}
