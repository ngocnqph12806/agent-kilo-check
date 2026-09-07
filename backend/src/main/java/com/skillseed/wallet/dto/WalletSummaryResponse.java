package com.skillseed.wallet.dto;

public record WalletSummaryResponse(
        int balance,
        int totalEarned,
        int totalSpent,
        int totalExpired,
        ExpiringSoonResponse expiringSoon,
        String tier) {
}