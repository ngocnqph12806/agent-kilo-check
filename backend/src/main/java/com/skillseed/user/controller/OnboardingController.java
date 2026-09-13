package com.skillseed.user.controller;

import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.domain.User;
import com.skillseed.user.dto.OnboardingCompleteResponse;
import com.skillseed.user.dto.OnboardingCompleteResponse.WalletGrant;
import com.skillseed.user.service.UserService;
import com.skillseed.wallet.dto.WalletSummaryResponse;
import com.skillseed.wallet.service.SeedWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Marks onboarding complete and grants the starter-seed pack. The
 * 7-step wizard's autosave writes flow through {@code PATCH /users/me};
 * this endpoint is called once at the end of the wizard (FR-M08).
 *
 * <p>After T-M412 this controller no longer touches
 * {@code SeedWalletRepository} directly — wallet reads go through
 * {@link SeedWalletService#getWalletSummary(UUID)}.
 */
@RestController
@RequestMapping("/api/v1/users/me/onboarding")
@Tag(name = "Users", description = "Onboarding completion + starter seed grant")
public class OnboardingController {

    private final UserService userService;
    private final SeedWalletService seedWalletService;

    public OnboardingController(
            UserService userService,
            SeedWalletService seedWalletService) {
        this.userService = userService;
        this.seedWalletService = seedWalletService;
    }

    @PostMapping
    @Operation(summary = "Mark onboarding as complete and grant 30 free starter seeds")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Onboarding completed; starter seeds granted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<OnboardingCompleteResponse> complete() {
        UUID userId = CurrentUser.requireId();
        User user = userService.markOnboardingComplete(userId);
        // grantStarterSeeds is idempotent and returns the GRANT ledger row;
        // the balance we report is the post-grant wallet summary.
        var grant = seedWalletService.grantStarterSeeds(userId);
        WalletSummaryResponse wallet = seedWalletService.getWalletSummary(userId);
        return ResponseEntity.ok(new OnboardingCompleteResponse(
                user.getId(),
                user.isOnboardingCompleted(),
                new WalletGrant(grant.getAmount(), grant.getExpiresAt(), wallet.balance())));
    }
}
