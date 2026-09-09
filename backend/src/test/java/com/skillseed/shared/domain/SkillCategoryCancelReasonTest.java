package com.skillseed.shared.domain;

import com.skillseed.booking.domain.CancelReason;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lock-in test for Sprint 5 (T-M401). The booking CancelReason gained
 * three new values so backend validation copies line up with
 * {@code screens-svg/04-booking/06-cancel.svg:48-58}.
 */
class CancelReasonSprint5Test {

    @Test
    void allCurrentValuesResolveFromDbValue() {
        for (CancelReason r : CancelReason.values()) {
            assertThat(CancelReason.fromValue(r.getDbValue())).isEqualTo(r);
        }
    }

    @Test
    void legacyEnumNameAliasStillResolves() {
        // Mid-Sprint we kept the original three enum names as a
        // backwards-compat alias. Confirm they still map.
        assertThat(CancelReason.fromValue("TEACHER_UNAVAILABLE"))
                .isEqualTo(CancelReason.TEACHER_UNAVAILABLE);
        assertThat(CancelReason.fromValue("LEARNER_UNAVAILABLE"))
                .isEqualTo(CancelReason.LEARNER_UNAVAILABLE);
        assertThat(CancelReason.fromValue("TECHNICAL_ISSUE"))
                .isEqualTo(CancelReason.TECHNICAL_ISSUE);
    }

    @Test
    void newSvgAlignedReasonsArePresent() {
        // The four reasons surfaced in the cancel modal SVG.
        assertThat(CancelReason.fromValue("schedule_conflict"))
                .isEqualTo(CancelReason.SCHEDULE_CONFLICT);
        assertThat(CancelReason.fromValue("found_another_mentor"))
                .isEqualTo(CancelReason.FOUND_ANOTHER_MENTOR);
        assertThat(CancelReason.fromValue("no_longer_needed"))
                .isEqualTo(CancelReason.NO_LONGER_NEEDED);
        assertThat(CancelReason.fromValue("other"))
                .isEqualTo(CancelReason.OTHER);
    }

    @Test
    void unknownValueFallsBackToOther() {
        // Forgiving default keeps the booking flow open even if FE ships a
        // new label before the enum is bumped.
        assertThat(CancelReason.fromValue("totally-unknown")).isEqualTo(CancelReason.OTHER);
        assertThat(CancelReason.fromValue(null)).isEqualTo(CancelReason.OTHER);
        assertThat(CancelReason.fromValue("")).isEqualTo(CancelReason.OTHER);
    }

    @Test
    void legacyMigrationMapsOldValues() {
        assertThat(CancelReason.migrateLegacy("TEACHER_UNAVAILABLE"))
                .isEqualTo(CancelReason.SCHEDULE_CONFLICT);
        assertThat(CancelReason.migrateLegacy("LEARNER_UNAVAILABLE"))
                .isEqualTo(CancelReason.NO_LONGER_NEEDED);
        assertThat(CancelReason.migrateLegacy("TECHNICAL_ISSUE"))
                .isEqualTo(CancelReason.OTHER);
        assertThat(CancelReason.migrateLegacy(null)).isEqualTo(CancelReason.OTHER);
    }
}
