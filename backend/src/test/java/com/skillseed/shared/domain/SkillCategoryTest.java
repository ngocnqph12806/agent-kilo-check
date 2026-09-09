package com.skillseed.shared.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lock-in tests for Sprint 5 (T-M402). The taxonomy gained two aliases —
 * {@code COOKING} and {@code ACADEMICS} — that match SVG filter labels
 * but map to existing canonical {@code dbValue}s so the seeded skill
 * rows stay in place.
 */
class SkillCategoryTest {

    @Test
    void legacyEightCategoriesRoundTrip() {
        for (SkillCategory c : SkillCategory.values()) {
            if (c == SkillCategory.COOKING || c == SkillCategory.ACADEMICS) {
                continue;
            }
            assertThat(SkillCategory.fromValue(c.getDbValue())).isEqualTo(c);
            assertThat(SkillCategory.fromValue(c.name())).isEqualTo(c);
        }
    }

    @Test
    void cookingAliasResolvesAndMapsToLifeCanonical() {
        SkillCategory c = SkillCategory.fromValue("cooking");
        assertThat(c).isEqualTo(SkillCategory.COOKING);
        assertThat(c.canonical()).isEqualTo("life");
    }

    @Test
    void academicsAliasResolvesAndMapsToBusinessCanonical() {
        SkillCategory c = SkillCategory.fromValue("academics");
        assertThat(c).isEqualTo(SkillCategory.ACADEMICS);
        assertThat(c.canonical()).isEqualTo("business");
    }

    @Test
    void fromValueMatchesDbValueOfAlias() {
        // Filtering clients that pass the db value (e.g. "life") must
        // still resolve to a category — round-trips to LIFE.
        assertThat(SkillCategory.fromValue("life")).isEqualTo(SkillCategory.LIFE);
    }

    @Test
    void fromValueIsCaseInsensitiveAndIgnoresWhitespace() {
        assertThat(SkillCategory.fromValue("  COOKING ")).isEqualTo(SkillCategory.COOKING);
        assertThat(SkillCategory.fromValue("Academics")).isEqualTo(SkillCategory.ACADEMICS);
    }

    @Test
    void unknownValueReturnsNull() {
        assertThat(SkillCategory.fromValue("rocket-science")).isNull();
        assertThat(SkillCategory.fromValue(null)).isNull();
        assertThat(SkillCategory.fromValue("")).isNull();
    }
}
