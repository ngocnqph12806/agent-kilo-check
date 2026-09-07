package com.skillseed.e2e;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * End-to-end smoke scaffold for Sprint 3 (T-M180).
 *
 * <p>Full happy path requires a live Daily.co API key, real wallets,
 * and WebSocket clients. Until we wire those into CI we keep this
 * scaffold disabled and rely on
 * {@code docs/MANUAL_E2E_SPRINT3.md} for operator sign-off.
 *
 * <p>When the sandbox has Docker (see
 * {@code AuthControllerIT} for the same pattern) this can be enabled
 * by removing {@link Disabled} and adding the bookings / session
 * stubs needed to drive the lifecycle.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Disabled("Requires Daily.co credentials and WebSocket client; see MANUAL_E2E_SPRINT3.md")
class Sprint3E2ESmokeIT {

    @Test
    @DisplayName("2-user happy path: register → onboard → book → join room → complete → rate")
    void happyPath() {
        // Intentionally left empty — see MANUAL_E2E_SPRINT3.md for the
        // operator-driven equivalent. The skeleton stays in tree so future
        // sprints can fill it in once the sandbox has the required infra.
    }
}
