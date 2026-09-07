package com.skillseed.user.controller;

import com.skillseed.shared.security.CurrentUser;
import com.skillseed.user.dto.OnboardingCompleteResponse;
import com.skillseed.user.dto.OnboardingCompleteResponse.WalletGrant;
import com.skillseed.user.domain.User;
import com.skillseed.user.service.UserService;
import com.skillseed.wallet.domain.SeedTransaction;
import com.skillseed.wallet.repository.SeedWalletRepository;
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
 */
@RestController
@RequestMapping("/api/v1/users/me/onboarding")
@Tag(name = "Users", description = "Onboarding completion + starter seed grant")
public class OnboardingController {

    private final UserService userService;
    private final SeedWalletService seedWalletService;
    private final SeedWalletRepository seedWalletRepository;

    public OnboardingController(
            UserService userService,
            SeedWalletService seedWalletService,
            SeedWalletRepository seedWalletRepository) {
        this.userService = userService;
        this.seedWalletService = seedWalletService;
        this.seedWalletRepository = seedWalletRepository;
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
        SeedTransaction grant = seedWalletService.grantStarterSeeds(userId);
        int balance = seedWalletRepository.findByUserId(userId)
                .map(w -> w.getBalanceCached())
                .orElse(grant.getBalanceAfter());
        return ResponseEntity.ok(new OnboardingCompleteResponse(
                user.getId(),
                user.isOnboardingCompleted(),
                new WalletGrant(grant.getAmount(), grant.getExpiresAt(), balance)));
    }
}