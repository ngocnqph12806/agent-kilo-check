package com.skillseed.wallet.service;

import com.skillseed.booking.domain.Booking;
import com.skillseed.shared.domain.SeedTransactionStatus;
import com.skillseed.shared.domain.SeedTransactionType;
import com.skillseed.user.domain.User;
import com.skillseed.user.repository.UserRepository;
import com.skillseed.wallet.domain.SeedTransaction;
import com.skillseed.wallet.domain.SeedWallet;
import com.skillseed.wallet.exception.WalletException;
import com.skillseed.wallet.repository.SeedTransactionRepository;
import com.skillseed.wallet.repository.SeedWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SeedWalletService}. Focuses on the ledger
 * invariants described in design.md §6:
 * <ul>
 *   <li>escrow holds the seed cost (status = PENDING, balance debited)</li>
 *   <li>release moves the spend to COMPLETED + emits EARN for the teacher</li>
 *   <li>refund honours the percentage and only spends the net delta</li>
 *   <li>expiry emits a paired EXPIRE row</li>
 *   <li>duplicate escrow / double-release is idempotent</li>
 * </ul>
 */
class SeedWalletServiceTest {

    private UserRepository userRepository;
    private SeedWalletRepository walletRepository;
    private SeedTransactionRepository txRepository;
    private SeedWalletService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        walletRepository = mock(SeedWalletRepository.class);
        txRepository = mock(SeedTransactionRepository.class);
        service = new SeedWalletService(userRepository, walletRepository, txRepository);
    }

    @Test
    void starterSeedsIsIdempotent() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        SeedWallet wallet = newWallet(userId, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(txRepository.existsByWalletUserIdAndDescription(userId,
                "Free starter seeds (onboarding)"))
                .thenReturn(true);
        SeedTransaction existing = new SeedTransaction(UUID.randomUUID(), wallet,
                SeedTransactionType.GRANT, 30, 30);
        when(txRepository.findFirstByWalletUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(existing));

        SeedTransaction result = service.grantStarterSeeds(userId);

        assertThat(result).isSameAs(existing);
        verify(txRepository, never()).save(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void escrowDebitMovesSpendToPendingAndDebitsBalance() {
        UUID learnerId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, teacherId, 30);

        SeedWallet wallet = newWallet(learnerId, 60);
        when(walletRepository.findByUserId(learnerId)).thenReturn(Optional.of(wallet));
        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of());

        SeedTransaction tx = service.escrowDebit(booking);

        assertThat(tx.getType()).isEqualTo(SeedTransactionType.SPEND);
        assertThat(tx.getStatus()).isEqualTo(SeedTransactionStatus.PENDING);
        assertThat(tx.getAmount()).isEqualTo(-30);
        assertThat(tx.getBalanceAfter()).isEqualTo(30);
        assertThat(tx.getBooking()).isSameAs(booking);
        assertThat(wallet.getBalanceCached()).isEqualTo(30);
        verify(txRepository).save(tx);
        verify(walletRepository).save(wallet);
    }

    @Test
    void escrowDebitRejectsWhenBalanceIsInsufficient() {
        UUID learnerId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, UUID.randomUUID(), 50);

        SeedWallet wallet = newWallet(learnerId, 10);
        when(walletRepository.findByUserId(learnerId)).thenReturn(Optional.of(wallet));
        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of());

        assertThatThrownBy(() -> service.escrowDebit(booking))
                .isInstanceOf(WalletException.class)
                .satisfies(ex -> assertThat(((WalletException) ex).getStatus().value())
                        .isEqualTo(409));
        verify(txRepository, never()).save(any());
    }

    @Test
    void escrowDebitIsIdempotentWhenSpendAlreadyExists() {
        UUID learnerId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, UUID.randomUUID(), 25);
        SeedWallet wallet = newWallet(learnerId, 100);
        SeedTransaction existing = new SeedTransaction(UUID.randomUUID(), wallet,
                SeedTransactionType.SPEND, -25, 75);
        existing.setStatus(SeedTransactionStatus.PENDING);

        when(walletRepository.findByUserId(learnerId)).thenReturn(Optional.of(wallet));
        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of(existing));

        SeedTransaction result = service.escrowDebit(booking);

        assertThat(result).isSameAs(existing);
        verify(txRepository, never()).save(any());
    }

    @Test
    void releaseEscrowCompletesSpendAndCreditsTeacher() {
        UUID learnerId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, teacherId, 40);
        SeedWallet learnerWallet = newWallet(learnerId, 60);
        SeedWallet teacherWallet = newWallet(teacherId, 5);
        SeedTransaction spend = new SeedTransaction(UUID.randomUUID(), learnerWallet,
                SeedTransactionType.SPEND, -40, 60);
        spend.setStatus(SeedTransactionStatus.PENDING);

        when(walletRepository.findByUserId(teacherId)).thenReturn(Optional.of(teacherWallet));
        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of(spend));

        SeedTransaction earn = service.releaseEscrow(booking);

        assertThat(spend.getStatus()).isEqualTo(SeedTransactionStatus.COMPLETED);
        assertThat(earn.getType()).isEqualTo(SeedTransactionType.EARN);
        assertThat(earn.getAmount()).isEqualTo(40);
        assertThat(earn.getBalanceAfter()).isEqualTo(45);
        assertThat(earn.getExpiresAt()).isAfter(Instant.now().plusSeconds(60 * 60 * 24 * 30));
        assertThat(teacherWallet.getBalanceCached()).isEqualTo(45);
        assertThat(teacherWallet.getTotalEarned()).isEqualTo(40);
        verify(txRepository, times(2)).save(any());
        verify(walletRepository).save(teacherWallet);
    }

    @Test
    void releaseEscrowIsIdempotentWhenSpendAlreadyCompleted() {
        UUID learnerId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, teacherId, 40);
        SeedWallet learnerWallet = newWallet(learnerId, 60);
        SeedTransaction spend = new SeedTransaction(UUID.randomUUID(), learnerWallet,
                SeedTransactionType.SPEND, -40, 60);
        spend.setStatus(SeedTransactionStatus.COMPLETED);

        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of(spend));

        SeedTransaction result = service.releaseEscrow(booking);

        assertThat(result).isSameAs(spend);
        verify(txRepository, never()).save(any());
    }

    @Test
    void fullRefundReturnsAllSeedsAndZeroesOutSpend() {
        UUID learnerId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, UUID.randomUUID(), 60);
        SeedWallet wallet = newWallet(learnerId, 40);
        SeedTransaction spend = new SeedTransaction(UUID.randomUUID(), wallet,
                SeedTransactionType.SPEND, -60, 40);
        spend.setStatus(SeedTransactionStatus.PENDING);

        when(walletRepository.findByUserId(learnerId)).thenReturn(Optional.of(wallet));
        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of(spend));

        SeedTransaction refund = service.refundEscrow(booking, 100);

        assertThat(spend.getStatus()).isEqualTo(SeedTransactionStatus.CANCELLED);
        assertThat(refund.getType()).isEqualTo(SeedTransactionType.REFUND);
        assertThat(refund.getAmount()).isEqualTo(60);
        assertThat(wallet.getBalanceCached()).isEqualTo(100);
        assertThat(wallet.getTotalSpent()).isEqualTo(0);
    }

    @Test
    void partialRefundKeepsHalfAsSpent() {
        UUID learnerId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, UUID.randomUUID(), 60);
        SeedWallet wallet = newWallet(learnerId, 40);
        wallet.setTotalSpent(60);
        SeedTransaction spend = new SeedTransaction(UUID.randomUUID(), wallet,
                SeedTransactionType.SPEND, -60, 40);
        spend.setStatus(SeedTransactionStatus.PENDING);

        when(walletRepository.findByUserId(learnerId)).thenReturn(Optional.of(wallet));
        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of(spend));

        SeedTransaction refund = service.refundEscrow(booking, 50);

        assertThat(refund.getAmount()).isEqualTo(30);
        assertThat(wallet.getBalanceCached()).isEqualTo(70);
        // previously counted 60 spent; only 30 is refunded so 30 remains spent
        assertThat(wallet.getTotalSpent()).isEqualTo(30);
    }

    @Test
    void forfeitEscrowMarksSpendCancelledAndIncrementsTotalSpent() {
        UUID learnerId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, UUID.randomUUID(), 30);
        SeedWallet wallet = newWallet(learnerId, 0);
        SeedTransaction spend = new SeedTransaction(UUID.randomUUID(), wallet,
                SeedTransactionType.SPEND, -30, 30);
        spend.setStatus(SeedTransactionStatus.PENDING);

        when(walletRepository.findByUserId(learnerId)).thenReturn(Optional.of(wallet));
        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of(spend));

        service.forfeitEscrow(booking);

        assertThat(spend.getStatus()).isEqualTo(SeedTransactionStatus.CANCELLED);
        assertThat(wallet.getTotalSpent()).isEqualTo(30);
    }

    @Test
    void forfeitEscrowIsIdempotentWhenAlreadyCancelled() {
        UUID learnerId = UUID.randomUUID();
        Booking booking = bookingStub(learnerId, UUID.randomUUID(), 30);
        SeedWallet wallet = newWallet(learnerId, 0);
        SeedTransaction spend = new SeedTransaction(UUID.randomUUID(), wallet,
                SeedTransactionType.SPEND, -30, 30);
        spend.setStatus(SeedTransactionStatus.CANCELLED);

        when(walletRepository.findByUserId(learnerId)).thenReturn(Optional.of(wallet));
        when(txRepository.findByBookingId(booking.getId())).thenReturn(List.of(spend));

        service.forfeitEscrow(booking);

        verify(walletRepository, never()).save(any());
    }

    @Test
    void processExpiryEmitsExpireRowForEachDueTransaction() {
        UUID userId = UUID.randomUUID();
        SeedWallet wallet = newWallet(userId, 100);
        SeedTransaction earn1 = new SeedTransaction(UUID.randomUUID(), wallet,
                SeedTransactionType.EARN, 50, 100);
        earn1.setExpiresAt(Instant.now().minusSeconds(60));
        SeedTransaction earn2 = new SeedTransaction(UUID.randomUUID(), wallet,
                SeedTransactionType.EARN, 30, 50);
        earn2.setExpiresAt(Instant.now().minusSeconds(60));
        earn2.setBalanceAfter(50);

        when(txRepository.findExpiringEarnTx(any(), any())).thenReturn(List.of(earn1, earn2));

        int processed = service.processExpiry();

        assertThat(processed).isEqualTo(2);
        ArgumentCaptor<SeedTransaction> captor = ArgumentCaptor.forClass(SeedTransaction.class);
        verify(txRepository, times(2)).save(captor.capture());
        List<SeedTransaction> saved = captor.getAllValues();
        assertThat(saved).extracting(SeedTransaction::getType)
                .containsOnly(SeedTransactionType.EXPIRE);
        assertThat(saved).extracting(SeedTransaction::getAmount)
                .containsExactly(-50, -30);
        assertThat(wallet.getBalanceCached()).isEqualTo(20);
        verify(walletRepository, times(2)).save(wallet);
    }

    @Test
    void getWalletSummaryReturnsZeroBalanceWhenNoTransactions() {
        UUID userId = UUID.randomUUID();
        SeedWallet wallet = newWallet(userId, 0);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(txRepository.sumActiveBalance(userId, any())).thenReturn(0);
        when(txRepository.sumExpiredAmount(userId)).thenReturn(0);
        when(txRepository.sumExpiringSoon(eqUserId(userId), any())).thenReturn(0);
        when(txRepository.findOldestExpiringAt(eqUserId(userId), any())).thenReturn(null);

        var summary = service.getWalletSummary(userId);

        assertThat(summary.balance()).isZero();
        assertThat(summary.totalEarned()).isZero();
        assertThat(summary.totalSpent()).isZero();
        assertThat(summary.totalExpired()).isZero();
        assertThat(summary.tier()).isEqualTo("bronze");
        assertThat(summary.expiringSoon().amount()).isZero();
    }

    private SeedWallet newWallet(UUID userId, int balance) {
        SeedWallet wallet = new SeedWallet();
        wallet.setUserId(userId);
        wallet.setBalanceCached(balance);
        wallet.setTotalEarned(0);
        wallet.setTotalSpent(0);
        wallet.setUpdatedAt(Instant.now());
        return wallet;
    }

    private Booking bookingStub(UUID learnerId, UUID teacherId, int seedAmount) {
        Booking booking = mock(Booking.class);
        when(booking.getId()).thenReturn(UUID.randomUUID());
        when(booking.getSeedAmount()).thenReturn(seedAmount);
        User learner = mock(User.class);
        when(learner.getId()).thenReturn(learnerId);
        User teacher = mock(User.class);
        when(teacher.getId()).thenReturn(teacherId);
        when(booking.getLearner()).thenReturn(learner);
        when(booking.getTeacher()).thenReturn(teacher);
        return booking;
    }

    private static UUID eqUserId(UUID id) {
        return id;
    }
}