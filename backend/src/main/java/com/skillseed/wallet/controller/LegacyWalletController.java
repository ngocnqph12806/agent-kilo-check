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
 * @deprecated use {@link WalletController} (plural {@code /wallets})
 *             which matches the spec §10 contract. Kept until all
 *             clients migrate.
 */
@Deprecated
@RestController
@RequestMapping("/api/v1/wallet")
@Tag(name = "Wallet (deprecated)")
public class LegacyWalletController {

    private final SeedWalletService seedWalletService;

    public LegacyWalletController(SeedWalletService seedWalletService) {
        this.seedWalletService = seedWalletService;
    }

    @GetMapping("/me")
    @Operation(summary = "DEPRECATED — use /api/v1/wallets/me")
    public ResponseEntity<WalletSummaryResponse> me() {
        UUID userId = CurrentUser.requireId();
        return ResponseEntity.ok(seedWalletService.getWalletSummary(userId));
    }

    @GetMapping("/me/transactions")
    @Operation(summary = "DEPRECATED — use /api/v1/wallets/me/transactions")
    public ResponseEntity<SeedTransactionPageResponse> transactions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        UUID userId = CurrentUser.requireId();
        return ResponseEntity.ok(
                seedWalletService.listTransactions(userId, page, size, null));
    }
}
