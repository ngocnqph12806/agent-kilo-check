package com.skillseed.skill.service;

import com.skillseed.shared.domain.SkillCategory;
import com.skillseed.shared.domain.SkillStatus;
import com.skillseed.skill.domain.Skill;
import com.skillseed.skill.dto.CreateCustomSkillRequest;
import com.skillseed.skill.dto.SkillResponse;
import com.skillseed.skill.exception.SkillException;
import com.skillseed.skill.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SkillService}. Covers search pagination, slug
 * derivation, status filtering for read endpoints, and the custom-skill
 * create flow including parent linking and duplicate-slug rejection.
 */
class SkillServiceTest {

    private SkillRepository repo;
    private SkillService service;

    @BeforeEach
    void setUp() {
        repo = mock(SkillRepository.class);
        service = new SkillService(repo);
    }

    private static Skill approved(UUID id, String slug, String name, SkillCategory category) {
        Skill skill = new Skill(id, slug, name, category);
        skill.setStatus(SkillStatus.APPROVED);
        skill.setCustom(false);
        return skill;
    }

    @Test
    void searchFiltersOutPendingAndRejectedSkills() {
        Skill approved = approved(UUID.randomUUID(), "java", "Java", SkillCategory.TECH);
        Skill pending = approved(UUID.randomUUID(), "rust", "Rust", SkillCategory.TECH);
        pending.setStatus(SkillStatus.PENDING_REVIEW);
        Skill rejected = approved(UUID.randomUUID(), "cobol", "Cobol", SkillCategory.TECH);
        rejected.setStatus(SkillStatus.REJECTED);

        when(repo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(approved, pending, rejected)));

        SkillService.SkillPage page = service.search(null, null, 0, 20);

        assertThat(page.content()).hasSize(1);
        assertThat(page.content().get(0).slug()).isEqualTo("java");
    }

    @Test
    void searchClampsNegativePageAndOversizedSize() {
        when(repo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.search(null, null, -5, 9999);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(captor.capture());
        Pageable pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(100);
    }

    @Test
    void searchAppliesZeroSizeDefault() {
        when(repo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.search(null, null, 0, 0);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void searchByQueryAndCategoryUsesCombinedRepo() {
        Skill java = approved(UUID.randomUUID(), "java", "Java", SkillCategory.TECH);
        when(repo.findByNameContainingIgnoreCaseAndCategory(
                eq("ja"), eq(SkillCategory.TECH), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(java)));

        SkillService.SkillPage page = service.search("ja", SkillCategory.TECH, 0, 20);

        assertThat(page.content()).hasSize(1);
        verify(repo).findByNameContainingIgnoreCaseAndCategory(
                "ja", SkillCategory.TECH, PageRequest.of(0, 20));
    }

    @Test
    void searchByQueryOnlyUsesNameRepo() {
        when(repo.findByNameContainingIgnoreCase(eq("rust"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.search("rust", null, 0, 20);

        verify(repo).findByNameContainingIgnoreCase("rust", PageRequest.of(0, 20));
    }

    @Test
    void searchByCategoryOnlyUsesCategoryRepo() {
        when(repo.findByCategory(eq(SkillCategory.MUSIC), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.search(null, SkillCategory.MUSIC, 0, 20);

        verify(repo).findByCategory(SkillCategory.MUSIC, PageRequest.of(0, 20));
    }

    @Test
    void getByIdThrowsNotFoundWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(SkillException.class)
                .satisfies(ex -> assertThat(((SkillException) ex).getCode())
                        .isEqualTo("SKILL_NOT_FOUND"));
    }

    @Test
    void getByIdReturnsDtoWhenFound() {
        UUID id = UUID.randomUUID();
        Skill skill = approved(id, "guitar", "Guitar", SkillCategory.MUSIC);
        when(repo.findById(id)).thenReturn(Optional.of(skill));

        SkillResponse response = service.getById(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.slug()).isEqualTo("guitar");
        assertThat(response.status()).isEqualTo("approved");
        assertThat(response.category()).isEqualTo("music");
        assertThat(response.custom()).isFalse();
    }

    @Test
    void createCustomSkillPersistsPendingReviewSlugAndCustomFlag() {
        when(repo.existsBySlug("kotlin-development")).thenReturn(false);

        SkillResponse response = service.createCustomSkill(
                new CreateCustomSkillRequest("Kotlin Development", "tech", null));

        ArgumentCaptor<Skill> captor = ArgumentCaptor.forClass(Skill.class);
        verify(repo).save(captor.capture());
        Skill saved = captor.getValue();
        assertThat(saved.getSlug()).isEqualTo("kotlin-development");
        assertThat(saved.getName()).isEqualTo("Kotlin Development");
        assertThat(saved.getCategory()).isEqualTo(SkillCategory.TECH);
        assertThat(saved.getStatus()).isEqualTo(SkillStatus.PENDING_REVIEW);
        assertThat(saved.isCustom()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(response.slug()).isEqualTo("kotlin-development");
        assertThat(response.status()).isEqualTo("pending_review");
        assertThat(response.custom()).isTrue();
    }

    @Test
    void createCustomSkillRejectsDuplicateSlug() {
        when(repo.existsBySlug("java")).thenReturn(true);

        assertThatThrownBy(() -> service.createCustomSkill(
                new CreateCustomSkillRequest("Java", "tech", null)))
                .isInstanceOf(SkillException.class)
                .satisfies(ex -> assertThat(((SkillException) ex).getCode())
                        .isEqualTo("SKILL_SLUG_EXISTS"));

        verify(repo, never()).save(any());
    }

    @Test
    void createCustomSkillRejectsEmptySlug() {
        assertThatThrownBy(() -> service.createCustomSkill(
                new CreateCustomSkillRequest("$$$", "tech", null)))
                .isInstanceOf(SkillException.class)
                .satisfies(ex -> assertThat(((SkillException) ex).getCode())
                        .isEqualTo("SKILL_SLUG_INVALID"));
    }

    @Test
    void createCustomSkillRejectsUnknownCategory() {
        assertThatThrownBy(() -> service.createCustomSkill(
                new CreateCustomSkillRequest("Foo", "spaceship", null)))
                .isInstanceOf(SkillException.class)
                .satisfies(ex -> assertThat(((SkillException) ex).getCode())
                        .isEqualTo("SKILL_CATEGORY_INVALID"));
    }

    @Test
    void createCustomSkillLinksParentWhenProvided() {
        UUID parentId = UUID.randomUUID();
        Skill parent = approved(parentId, "programming", "Programming", SkillCategory.TECH);
        when(repo.existsBySlug("oop")).thenReturn(false);
        when(repo.findById(parentId)).thenReturn(Optional.of(parent));

        service.createCustomSkill(new CreateCustomSkillRequest("OOP", "tech", parentId));

        ArgumentCaptor<Skill> captor = ArgumentCaptor.forClass(Skill.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getParent()).isSameAs(parent);
        assertThat(captor.getValue().getParentId()).isEqualTo(parentId);
    }

    @Test
    void createCustomSkillFailsWhenParentMissing() {
        UUID parentId = UUID.randomUUID();
        when(repo.existsBySlug("oop")).thenReturn(false);
        when(repo.findById(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createCustomSkill(
                new CreateCustomSkillRequest("OOP", "tech", parentId)))
                .isInstanceOf(SkillException.class)
                .satisfies(ex -> assertThat(((SkillException) ex).getCode())
                        .isEqualTo("PARENT_NOT_FOUND"));
    }

    @Test
    void toSlugNormalisesUnicodeAndPunctuation() {
        assertThat(SkillService.toSlug("Cà phê sữa đá!")).isEqualTo("ca-phe-sua-da");
        assertThat(SkillService.toSlug("  __Hello World__ ")).isEqualTo("hello-world");
        assertThat(SkillService.toSlug("Lập trình")).isEqualTo("lap-trinh");
        assertThat(SkillService.toSlug("")).isEqualTo("");
    }

    @Test
    void responseCarriesStatusStringFromEnum() {
        Skill pending = approved(UUID.randomUUID(), "x", "X", SkillCategory.TECH);
        pending.setStatus(SkillStatus.PENDING_REVIEW);

        when(repo.findById(pending.getId())).thenReturn(Optional.of(pending));

        SkillResponse response = service.getById(pending.getId());
        assertThat(response.status()).isEqualTo("pending_review");
    }
}
