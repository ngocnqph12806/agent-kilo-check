package com.skillseed.wallet.controller;

import com.skillseed.shared.security.CurrentUser;
import com.skillseed.wallet.dto.SeedTransactionPageResponse;
import com.skillseed.wallet.dto.WalletSummaryResponse;
import com.skillseed.wallet.service.SeedWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Wallet read endpoints (T-M126). Mutations (escrow / release / refund /
 * expiry) are internal APIs called from the booking module; the public
 * surface only exposes what the user themselves can see.
 */
@RestController
@RequestMapping("/api/v1/wallet")
@Tag(name = "Wallet", description = "Seed wallet balance and ledger history")
public class WalletController {

    private final SeedWalletService seedWalletService;

    public WalletController(SeedWalletService seedWalletService) {
        this.seedWalletService = seedWalletService;
    }

    @GetMapping("/me")
    @Operation(summary = "Current user's wallet summary (balance, totals, tier, expiring soon)")
    public ResponseEntity<WalletSummaryResponse> me() {
        UUID userId = CurrentUser.requireId();
        return ResponseEntity.ok(seedWalletService.getWalletSummary(userId));
    }

    @GetMapping("/me/transactions")
    @Operation(summary = "Current user's transaction history, newest first")
    public ResponseEntity<SeedTransactionPageResponse> transactions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        UUID userId = CurrentUser.requireId();
        return ResponseEntity.ok(seedWalletService.listTransactions(userId, page, size));
    }
}