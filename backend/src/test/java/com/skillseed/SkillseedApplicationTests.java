package com.skillseed;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Placeholder for the full Spring context smoke test.
 *
 * <p>Disabled by default because the application context requires a
 * live PostgreSQL + Redis instance. The context-loading behaviour is
 * covered by {@code AuthControllerIntegrationTest} (T-M61) which boots
 * the context against a Testcontainers Postgres instance.
 */
class SkillseedApplicationTests {

    @Test
    @Disabled("Requires Postgres + Redis; covered by AuthControllerIntegrationTest (T-M61)")
    void contextLoads() {
    }
}
