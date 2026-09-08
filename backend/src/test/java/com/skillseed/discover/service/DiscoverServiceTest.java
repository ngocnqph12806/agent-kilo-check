package com.skillseed.discover.service;

import com.skillseed.discover.dto.DiscoverMatchResponse;
import com.skillseed.discover.dto.DiscoverPageResponse;
import com.skillseed.discover.repository.DiscoverRepository;
import com.skillseed.shared.domain.AuthProvider;
import com.skillseed.skill.domain.Skill;
import com.skillseed.shared.domain.SkillCategory;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserSkillWanted;
import com.skillseed.user.repository.UserSkillWantedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DiscoverServiceTest {

    private DiscoverRepository repo;
    private UserSkillWantedRepository wantedRepo;
    private DiscoverService service;

    @BeforeEach
    void setUp() {
        repo = mock(DiscoverRepository.class);
        wantedRepo = mock(UserSkillWantedRepository.class);
        service = new DiscoverService(repo, wantedRepo);
    }

    private static User user(UUID id) {
        User u = new User(id, "u" + id + "@example.com", "User " + id);
        u.setAuthProvider(AuthProvider.EMAIL);
        return u;
    }

    private static UserSkillWanted wanted(User u, Skill s) {
        return new UserSkillWanted(UUID.randomUUID(), u, s);
    }

    @Test
    void noWantedSkillsReturnsEmptyPage() {
        User caller = user(UUID.randomUUID());
        when(wantedRepo.findByUserId(caller.getId())).thenReturn(List.of());

        DiscoverPageResponse response = service.discover(caller, null, null, null, null, 0, 20);

        assertThat(response.items()).isEmpty();
        assertThat(response.totalElements()).isZero();
    }

    @Test
    void clampsPageAndSizeAndDelegatesToRepository() {
        User caller = user(UUID.randomUUID());
        Skill java = new Skill(UUID.randomUUID(), "java", "Java", SkillCategory.TECH);
        when(wantedRepo.findByUserId(caller.getId())).thenReturn(List.of(wanted(caller, java)));
        when(repo.findMatches(eq(caller.getId()), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        service.discover(caller, null, null, null, null, -1, 9999);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        org.mockito.Mockito.verify(repo).findMatches(
                eq(caller.getId()), any(), any(), any(), any(), any(),
                captor.capture());
        Pageable used = captor.getValue();
        assertThat(used.getPageNumber()).isZero();
        assertThat(used.getPageSize()).isEqualTo(50);
    }

    @Test
    void passesWantedSkillIdsDownstream() {
        User caller = user(UUID.randomUUID());
        Skill java = new Skill(UUID.randomUUID(), "java", "Java", SkillCategory.TECH);
        Skill guitar = new Skill(UUID.randomUUID(), "guitar", "Guitar", SkillCategory.MUSIC);
        when(wantedRepo.findByUserId(caller.getId()))
                .thenReturn(List.of(wanted(caller, java), wanted(caller, guitar)));
        when(repo.findMatches(eq(caller.getId()), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        service.discover(caller, null, "en", "VN", new BigDecimal("4.0"), 0, 20);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UUID>> skillsCaptor = ArgumentCaptor.forClass(List.class);
        org.mockito.Mockito.verify(repo).findMatches(
                eq(caller.getId()), skillsCaptor.capture(), eq(null),
                eq("en"), eq("VN"), eq(new BigDecimal("4.0")), any());
        assertThat(skillsCaptor.getValue()).containsExactly(java.getId(), guitar.getId());
    }

    @Test
    void mapsRepositoryPageToPageResponse() {
        User caller = user(UUID.randomUUID());
        Skill java = new Skill(UUID.randomUUID(), "java", "Java", SkillCategory.TECH);
        when(wantedRepo.findByUserId(caller.getId())).thenReturn(List.of(wanted(caller, java)));
        DiscoverMatchResponse match = new DiscoverMatchResponse(
                UUID.randomUUID(), "Bob", null, "VN", List.of("en"),
                "hi", new BigDecimal("4.5"), 7, 1, List.of());
        when(repo.findMatches(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(match), PageRequest.of(1, 5), 23));

        DiscoverPageResponse response = service.discover(caller, null, null, null, null, 1, 5);

        assertThat(response.items()).hasSize(1);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.totalElements()).isEqualTo(23);
        assertThat(response.totalPages()).isEqualTo(5);
    }
}
