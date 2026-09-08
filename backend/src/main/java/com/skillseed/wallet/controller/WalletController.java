package com.skillseed.wallet.controller;

import com.skillseed.shared.domain.SeedTransactionType;
import com.skillseed.shared.security.CurrentUser;
import com.skillseed.wallet.dto.SeedTransactionPageResponse;
import com.skillseed.wallet.dto.WalletSummaryResponse;
import com.skillseed.wallet.service.SeedWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Wallet read endpoints (T-M126). Mutations (escrow / capture / refund /
 * expiry) are internal APIs called from the booking module; the public
 * surface only exposes what the user themselves can see.
 *
 * <p>Per spec §10, the canonical path is {@code /api/v1/wallets}. The
 * singular {@code /api/v1/wallet} form is kept as a deprecated alias
 * for clients that have not yet migrated.
 */
@RestController
@RequestMapping("/api/v1/wallets")
@Validated
@Tag(name = "Wallet", description = "Seed wallet balance and ledger history")
public class WalletController {

    private final SeedWalletService seedWalletService;

    public WalletController(SeedWalletService seedWalletService) {
        this.seedWalletService = seedWalletService;
    }

    @GetMapping("/me")
    @Operation(summary = "Current user's wallet summary (balance, totals, tier, expiring soon, pending hold)")
    public ResponseEntity<WalletSummaryResponse> me() {
        UUID userId = CurrentUser.requireId();
        return ResponseEntity.ok(seedWalletService.getWalletSummary(userId));
    }

    @GetMapping("/me/transactions")
    @Operation(summary = "Current user's transaction history, newest first; optional type filter")
    public ResponseEntity<SeedTransactionPageResponse> transactions(
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(name = "type", required = false) List<SeedTransactionType> types) {
        UUID userId = CurrentUser.requireId();
        return ResponseEntity.ok(
                seedWalletService.listTransactions(userId, page, size, types));
    }
}
