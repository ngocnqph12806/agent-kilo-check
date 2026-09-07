package com.skillseed.user.service;

import com.skillseed.user.domain.User;
import com.skillseed.user.domain.UserSkillOffered;
import com.skillseed.user.dto.CurrentUserResponse;
import com.skillseed.user.dto.CurrentUserResponse.OfferedSkill;
import com.skillseed.user.dto.CurrentUserResponse.SkillDnaSummary;
import com.skillseed.user.dto.CurrentUserResponse.WalletSummary;
import com.skillseed.user.dto.PublicUserResponse;
import com.skillseed.user.dto.UpdateProfileRequest;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.user.repository.UserSkillOfferedRepository;
import com.skillseed.user.repository.UserSkillWantedRepository;
import com.skillseed.wallet.domain.SeedWallet;
import com.skillseed.wallet.repository.SeedWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Application service for the user module: reads current and public
 * profiles, updates profile fields, and supports the onboarding flow.
 *
 * <p>Wallet credit operations live in {@link com.skillseed.wallet.service.SeedWalletService}
 * — this service orchestrates them in the onboarding step.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserSkillOfferedRepository userSkillOfferedRepository;
    private final UserSkillWantedRepository userSkillWantedRepository;
    private final SeedWalletRepository seedWalletRepository;

    public UserService(
            UserRepository userRepository,
            UserSkillOfferedRepository userSkillOfferedRepository,
            UserSkillWantedRepository userSkillWantedRepository,
            SeedWalletRepository seedWalletRepository) {
        this.userRepository = userRepository;
        this.userSkillOfferedRepository = userSkillOfferedRepository;
        this.userSkillWantedRepository = userSkillWantedRepository;
        this.seedWalletRepository = seedWalletRepository;
    }

    /** Returns the authenticated user's full profile + Skill DNA summary. */
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(UUID userId) {
        User user = loadActiveUser(userId);
        return toCurrentUserResponse(user);
    }

    /** Returns the public profile of another user. */
    @Transactional(readOnly = true)
    public PublicUserResponse getPublicProfile(UUID userId) {
        User user = loadActiveUser(userId);
        List<UserSkillOffered> offered = userSkillOfferedRepository
                .findByUserIdAndActiveTrue(userId);
        List<PublicUserResponse.OfferedSkill> skills = offered.stream()
                .map(this::toPublicOfferedSkill)
                .toList();
        return new PublicUserResponse(
                user.getId(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getCountryCode(),
                user.getTimezone(),
                user.getLanguages() == null ? List.of() : List.of(user.getLanguages()),
                user.getLearningStyle(),
                user.isVerified(),
                user.getVerificationLevel(),
                user.isOnboardingCompleted(),
                user.getRatingAvg(),
                user.getSessionsCompleted(),
                user.getCreatedAt(),
                skills);
    }

    /** Applies a partial update to the current user's profile. */
    @Transactional
    public CurrentUserResponse updateProfile(UUID userId, UpdateProfileRequest req) {
        User user = loadActiveUser(userId);
        if (req.fullName() != null) {
            user.setFullName(req.fullName().trim());
        }
        if (req.bio() != null) {
            user.setBio(req.bio());
        }
        if (req.countryCode() != null) {
            user.setCountryCode(req.countryCode().toUpperCase(Locale.ROOT));
        }
        if (req.timezone() != null) {
            user.setTimezone(req.timezone());
        }
        if (req.languages() != null) {
            user.setLanguages(req.languages().toArray(new String[0]));
        }
        if (req.learningStyle() != null) {
            user.setLearningStyle(req.learningStyle());
        }
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("Updated profile for user id={}", userId);
        return toCurrentUserResponse(user);
    }

    /**
     * Marks the current user as having completed onboarding. Idempotent.
     * Wallet grant happens in the caller so the credit and the
     * onboarding flag flip are part of the same transaction.
     */
    @Transactional
    public User markOnboardingComplete(UUID userId) {
        User user = loadActiveUser(userId);
        if (user.isOnboardingCompleted()) {
            return user;
        }
        user.setOnboardingCompleted(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("Onboarding completed for user id={}", userId);
        return user;
    }

    User loadActiveUser(UUID userId) {
        return userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> UserException.notFound("USER_NOT_FOUND",
                        "User not found"));
    }

    CurrentUserResponse toCurrentUserResponse(User user) {
        List<UserSkillOffered> offered = userSkillOfferedRepository
                .findByUserIdAndActiveTrue(user.getId());
        List<OfferedSkill> offeredDtos = offered.stream()
                .map(this::toCurrentOfferedSkill)
                .toList();
        int wantedCount = userSkillWantedRepository.findByUserId(user.getId()).size();

        WalletSummary walletSummary = seedWalletRepository.findByUserId(user.getId())
                .map(this::toWalletSummary)
                .orElseGet(() -> new WalletSummary(0, 0, 0));

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getCountryCode(),
                user.getTimezone(),
                user.getLanguages() == null ? List.of() : List.of(user.getLanguages()),
                user.getLearningStyle(),
                user.getAuthProvider().name().toLowerCase(Locale.ROOT),
                user.isVerified(),
                user.getVerificationLevel(),
                user.isOnboardingCompleted(),
                user.getRatingAvg(),
                user.getSessionsCompleted(),
                user.getCreatedAt(),
                new SkillDnaSummary(offeredDtos, wantedCount),
                walletSummary);
    }

    private OfferedSkill toCurrentOfferedSkill(UserSkillOffered uso) {
        var skill = uso.getSkill();
        return new OfferedSkill(
                uso.getId(),
                skill.getId(),
                skill.getSlug(),
                skill.getName(),
                skill.getCategory().getDbValue(),
                uso.getLevel(),
                uso.getYearsExperience(),
                uso.getDescription(),
                uso.getHourlySeedRate());
    }

    private PublicUserResponse.OfferedSkill toPublicOfferedSkill(UserSkillOffered uso) {
        var skill = uso.getSkill();
        return new PublicUserResponse.OfferedSkill(
                uso.getId(),
                skill.getId(),
                skill.getSlug(),
                skill.getName(),
                skill.getCategory().getDbValue(),
                uso.getLevel(),
                uso.getYearsExperience(),
                uso.getHourlySeedRate());
    }

    private WalletSummary toWalletSummary(SeedWallet wallet) {
        return new WalletSummary(
                wallet.getBalanceCached(),
                wallet.getTotalEarned(),
                wallet.getTotalSpent());
    }
}