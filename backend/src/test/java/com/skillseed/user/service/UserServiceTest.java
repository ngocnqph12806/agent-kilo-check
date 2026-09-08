package com.skillseed.user.service;

import com.skillseed.shared.domain.AuthProvider;
import com.skillseed.skill.domain.Skill;
import com.skillseed.shared.domain.SkillCategory;
import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserAvailability;
import com.skillseed.user.domain.UserSkillOffered;
import com.skillseed.user.dto.CurrentUserResponse;
import com.skillseed.user.dto.FreeSlotResponse;
import com.skillseed.user.dto.PublicUserResponse;
import com.skillseed.user.dto.UpdateProfileRequest;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserAvailabilityRepository;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.user.repository.UserSkillOfferedRepository;
import com.skillseed.user.repository.UserSkillWantedRepository;
import com.skillseed.wallet.domain.SeedWallet;
import com.skillseed.wallet.repository.SeedWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserService}. Covers current profile lookup,
 * public profile mapping, partial updates, onboarding completion, and
 * the soft-delete-aware user loader.
 */
class UserServiceTest {

    private UserRepository userRepository;
    private UserSkillOfferedRepository offeredRepo;
    private UserSkillWantedRepository wantedRepo;
    private SeedWalletRepository walletRepo;
    private UserAvailabilityRepository availabilityRepo;
    private UserService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        offeredRepo = mock(UserSkillOfferedRepository.class);
        wantedRepo = mock(UserSkillWantedRepository.class);
        walletRepo = mock(SeedWalletRepository.class);
        availabilityRepo = mock(UserAvailabilityRepository.class);
        service = new UserService(userRepository, offeredRepo, wantedRepo, walletRepo,
                availabilityRepo);
    }

    private static User activeUser(UUID id) {
        User user = new User(id, "alice@example.com", "Alice");
        user.setAuthProvider(AuthProvider.EMAIL);
        user.setVerified(true);
        user.setVerificationLevel((short) 1);
        user.setOnboardingCompleted(true);
        user.setBio("hi");
        user.setCountryCode("VN");
        user.setTimezone("Asia/Ho_Chi_Minh");
        user.setLanguages(new String[]{"en", "vi"});
        user.setRatingAvg(new BigDecimal("4.5"));
        user.setSessionsCompleted(7);
        user.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        user.setUpdatedAt(Instant.parse("2026-02-01T00:00:00Z"));
        return user;
    }

    @Test
    void getCurrentUserMapsWalletAndSkills() {
        UUID id = UUID.randomUUID();
        User user = activeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(offeredRepo.findByUserIdAndActiveTrue(id)).thenReturn(List.of());
        when(wantedRepo.findByUserId(id)).thenReturn(List.of());

        SeedWallet wallet = new SeedWallet(user);
        wallet.setBalanceCached(120);
        wallet.setTotalEarned(300);
        wallet.setTotalSpent(180);
        when(walletRepo.findByUserId(id)).thenReturn(Optional.of(wallet));

        CurrentUserResponse response = service.getCurrentUser(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.wallet().balance()).isEqualTo(120);
        assertThat(response.wallet().totalEarned()).isEqualTo(300);
        assertThat(response.wallet().totalSpent()).isEqualTo(180);
        assertThat(response.skillDna().wantedCount()).isZero();
        assertThat(response.skillDna().offered()).isEmpty();
    }

    @Test
    void getCurrentUserReturnsZeroWalletWhenAbsent() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(activeUser(id)));
        when(offeredRepo.findByUserIdAndActiveTrue(id)).thenReturn(List.of());
        when(wantedRepo.findByUserId(id)).thenReturn(List.of());
        when(walletRepo.findByUserId(id)).thenReturn(Optional.empty());

        CurrentUserResponse response = service.getCurrentUser(id);

        assertThat(response.wallet().balance()).isZero();
        assertThat(response.wallet().totalEarned()).isZero();
        assertThat(response.wallet().totalSpent()).isZero();
    }

    @Test
    void getCurrentUserMapsOfferedSkillToDna() {
        UUID id = UUID.randomUUID();
        User user = activeUser(id);
        Skill skill = new Skill(UUID.randomUUID(), "java", "Java", SkillCategory.TECH);
        skill.setStatus(com.skillseed.shared.domain.SkillStatus.APPROVED);
        UserSkillOffered uso = new UserSkillOffered(UUID.randomUUID(), user, skill, (short) 4);
        uso.setYearsExperience(5);
        uso.setHourlySeedRate(60);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(offeredRepo.findByUserIdAndActiveTrue(id)).thenReturn(List.of(uso));
        when(wantedRepo.findByUserId(id)).thenReturn(List.of());
        when(walletRepo.findByUserId(id)).thenReturn(Optional.empty());

        CurrentUserResponse response = service.getCurrentUser(id);

        assertThat(response.skillDna().offered()).hasSize(1);
        CurrentUserResponse.OfferedSkill offered = response.skillDna().offered().get(0);
        assertThat(offered.skillSlug()).isEqualTo("java");
        assertThat(offered.category()).isEqualTo("tech");
        assertThat(offered.level()).isEqualTo((short) 4);
        assertThat(offered.hourlySeedRate()).isEqualTo(60);
    }

    @Test
    void getCurrentUserFailsForMissingUser() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCurrentUser(id))
                .isInstanceOf(UserException.class)
                .satisfies(ex -> assertThat(((UserException) ex).getCode())
                        .isEqualTo("USER_NOT_FOUND"));
    }

    @Test
    void getCurrentUserSkipsSoftDeletedUsers() {
        UUID id = UUID.randomUUID();
        User deleted = activeUser(id);
        deleted.setDeletedAt(Instant.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> service.getCurrentUser(id))
                .isInstanceOf(UserException.class)
                .satisfies(ex -> assertThat(((UserException) ex).getCode())
                        .isEqualTo("USER_NOT_FOUND"));
    }

    @Test
    void publicProfileExcludesPrivateFields() {
        UUID id = UUID.randomUUID();
        User user = activeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(offeredRepo.findByUserIdAndActiveTrue(id)).thenReturn(List.of());

        PublicUserResponse response = service.getPublicProfile(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.fullName()).isEqualTo("Alice");
        assertThat(response.bio()).isEqualTo("hi");
        assertThat(response.countryCode()).isEqualTo("VN");
        assertThat(response.languages()).containsExactly("en", "vi");
    }

    @Test
    void updateProfileAppliesNonNullFieldsAndPersists() {
        UUID id = UUID.randomUUID();
        User user = activeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UpdateProfileRequest req = new UpdateProfileRequest(
                "  Alice Updated  ",
                "New bio",
                "us",
                "America/New_York",
                List.of("en"),
                "visual"
        );

        service.updateProfile(id, req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getFullName()).isEqualTo("Alice Updated");
        assertThat(saved.getBio()).isEqualTo("New bio");
        assertThat(saved.getCountryCode()).isEqualTo("US");
        assertThat(saved.getTimezone()).isEqualTo("America/New_York");
        assertThat(saved.getLanguages()).containsExactly("en");
        assertThat(saved.getLearningStyle()).isEqualTo("visual");
    }

    @Test
    void updateProfileLeavesUntouchedFieldsAlone() {
        UUID id = UUID.randomUUID();
        User user = activeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        service.updateProfile(id, new UpdateProfileRequest(
                null, null, null, null, null, null));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getBio()).isEqualTo("hi");
        assertThat(saved.getCountryCode()).isEqualTo("VN");
        assertThat(saved.getLearningStyle()).isNull();
    }

    @Test
    void markOnboardingCompleteFlipsAndSaves() {
        UUID id = UUID.randomUUID();
        User user = activeUser(id);
        user.setOnboardingCompleted(false);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = service.markOnboardingComplete(id);

        assertThat(result.isOnboardingCompleted()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void markOnboardingCompleteIsIdempotent() {
        UUID id = UUID.randomUUID();
        User user = activeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        service.markOnboardingComplete(id);
        verify(userRepository).save(user);

        org.mockito.Mockito.reset(userRepository);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        service.markOnboardingComplete(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getFreeSlotsReturnsEmptyForUserWithoutAvailability() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(activeUser(id)));
        when(availabilityRepo.findByUserId(id)).thenReturn(List.of());

        List<FreeSlotResponse> slots = service.getFreeSlots(id, Instant.now(), 7);

        assertThat(slots).isEmpty();
    }

    @Test
    void getFreeSlotsExpandsWeeklyRulesIntoConcreteUtcSlots() {
        UUID id = UUID.randomUUID();
        User user = activeUser(id);
        user.setTimezone("UTC");
        UserAvailability rule = new UserAvailability(
                UUID.randomUUID(), user, (short) 0,
                LocalTime.of(9, 0), LocalTime.of(12, 0), "UTC");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(availabilityRepo.findByUserId(id)).thenReturn(List.of(rule));

        Instant from = Instant.parse("2026-01-04T00:00:00Z");
        List<FreeSlotResponse> slots = service.getFreeSlots(id, from, 8);

        assertThat(slots).hasSize(2);
        for (FreeSlotResponse slot : slots) {
            assertThat(slot.sourceDow()).isZero();
            assertThat(slot.timezone()).isEqualTo("UTC");
        }
    }
}
