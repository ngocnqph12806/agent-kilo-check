package com.skillseed.user.service;

import com.skillseed.booking.repository.BookingRepository;
import com.skillseed.notification.repository.NotificationRepository;
import com.skillseed.rating.repository.RatingRepository;
import com.skillseed.user.domain.User;
import com.skillseed.user.dto.UserDataExportResponse;
import com.skillseed.user.exception.UserException;
import com.skillseed.user.repository.UserAvailabilityRepository;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.user.repository.UserSkillOfferedRepository;
import com.skillseed.user.repository.UserSkillWantedRepository;
import com.skillseed.wallet.repository.SeedTransactionRepository;
import com.skillseed.wallet.repository.SeedWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserGdprServiceTest {

    private static final UUID USER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    private UserRepository userRepository;
    private UserSkillOfferedRepository userSkillOfferedRepository;
    private UserSkillWantedRepository userSkillWantedRepository;
    private UserAvailabilityRepository userAvailabilityRepository;
    private SeedWalletRepository seedWalletRepository;
    private SeedTransactionRepository seedTransactionRepository;
    private BookingRepository bookingRepository;
    private RatingRepository ratingRepository;
    private NotificationRepository notificationRepository;
    private UserGdprService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userSkillOfferedRepository = mock(UserSkillOfferedRepository.class);
        userSkillWantedRepository = mock(UserSkillWantedRepository.class);
        userAvailabilityRepository = mock(UserAvailabilityRepository.class);
        seedWalletRepository = mock(SeedWalletRepository.class);
        seedTransactionRepository = mock(SeedTransactionRepository.class);
        bookingRepository = mock(BookingRepository.class);
        ratingRepository = mock(RatingRepository.class);
        notificationRepository = mock(NotificationRepository.class);

        service = new UserGdprService(
                userRepository,
                userSkillOfferedRepository,
                userSkillWantedRepository,
                userAvailabilityRepository,
                seedWalletRepository,
                seedTransactionRepository,
                bookingRepository,
                ratingRepository,
                notificationRepository);
    }

    @Test
    void softDeleteAnonymisesAndPersists() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(USER_ID);
        when(user.getDeletedAt()).thenReturn(null);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userSkillOfferedRepository.findByUserIdAndActiveTrue(USER_ID)).thenReturn(List.of());
        when(userSkillWantedRepository.findByUserId(USER_ID)).thenReturn(List.of());

        service.softDelete(USER_ID);

        verify(user).setFullName("Deleted user");
        verify(user).setEmail(org.mockito.ArgumentMatchers.contains("deleted-"));
        verify(user).setPhone(null);
        verify(user).setAvatarUrl(null);
        verify(user).setBio(null);
        verify(user).setDeletedAt(any(Instant.class));
        verify(userRepository).save(user);
        verify(userAvailabilityRepository).deleteByUserId(USER_ID);
    }

    @Test
    void softDeleteIsIdempotent() {
        User user = mock(User.class);
        when(user.getDeletedAt()).thenReturn(Instant.now());
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        service.softDelete(USER_ID);

        verify(user, never()).setFullName(any());
        verify(userRepository, never()).save(any());
        verify(userAvailabilityRepository, never()).deleteByUserId(any());
    }

    @Test
    void softDeleteThrowsWhenUserMissing() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.softDelete(USER_ID))
                .isInstanceOf(UserException.class);
    }

    @Test
    void exportReturnsProfileOnlyWhenNoRelatedData() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(USER_ID);
        when(user.getEmail()).thenReturn("test@x.com");
        when(user.getFullName()).thenReturn("Alice");
        when(user.getLanguages()).thenReturn(new String[]{"en", "vi"});
        when(user.getTimezone()).thenReturn("Asia/Ho_Chi_Minh");
        when(user.getAuthProvider()).thenReturn(com.skillseed.shared.domain.AuthProvider.EMAIL);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userSkillOfferedRepository.findByUserIdAndActiveTrue(USER_ID)).thenReturn(List.of());
        when(userSkillWantedRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(userAvailabilityRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(seedWalletRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        Page<com.skillseed.booking.domain.Booking> emptyPage = new PageImpl<>(List.of());
        when(bookingRepository.findByTeacherId(eq(USER_ID), any(Pageable.class))).thenReturn(emptyPage);
        when(bookingRepository.findByLearnerId(eq(USER_ID), any(Pageable.class))).thenReturn(emptyPage);
        when(ratingRepository.findByRaterId(USER_ID)).thenReturn(List.of());
        when(ratingRepository.findByRateeId(eq(USER_ID), any(Pageable.class))).thenReturn(emptyPage);

        UserDataExportResponse export = service.export(USER_ID);

        assertThat(export.profile().email()).isEqualTo("test@x.com");
        assertThat(export.offeredSkills()).isEmpty();
        assertThat(export.wantedSkills()).isEmpty();
        assertThat(export.bookingsAsTeacher()).isEmpty();
        assertThat(export.ratingsGiven()).isEmpty();
        assertThat(export.exportedAt()).isNotNull();
    }

    @Test
    void exportThrowsWhenUserMissing() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.export(USER_ID))
                .isInstanceOf(UserException.class);
    }

    @Test
    void purgeExpiredDeletedUsersDeletesOldAndKeepsFresh() {
        User expired = mock(User.class);
        when(expired.getId()).thenReturn(USER_ID);
        when(expired.getDeletedAt()).thenReturn(Instant.now().minus(31, ChronoUnit.DAYS));

        User fresh = mock(User.class);
        when(fresh.getId()).thenReturn(UUID.randomUUID());
        when(fresh.getDeletedAt()).thenReturn(Instant.now().minus(5, ChronoUnit.DAYS));

        when(userRepository.findAll()).thenReturn(List.of(expired, fresh));

        int count = service.purgeExpiredDeletedUsers();

        assertThat(count).isEqualTo(1);
        verify(userRepository, times(1)).delete(expired);
        verify(userRepository, never()).delete(fresh);
    }
}