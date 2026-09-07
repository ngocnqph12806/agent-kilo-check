package com.skillseed.wallet.dto;

/**
 * Wallet tier derived from lifetime net earnings (FR-M36).
 *
 * <p>Thresholds chosen for MVP and may be revised once we have real usage
 * data. The tier is computed on the fly from the wallet row, never
 * persisted, so the ladder can be changed without a migration.
 */
public enum WalletTier {
    BRONZE(0, 0),
    SILVER(200, 200),
    GOLD(1_000, 1_000),
    PLATINUM(5_000, 5_000);

    private final int minNetEarned;
    private final int displayOrder;

    WalletTier(int minNetEarned, int displayOrder) {
        this.minNetEarned = minNetEarned;
        this.displayOrder = displayOrder;
    }

    public String getDbValue() {
        return name().toLowerCase();
    }

    public static WalletTier fromNetEarned(int netEarned) {
        WalletTier current = BRONZE;
        for (WalletTier tier : values()) {
            if (netEarned >= tier.minNetEarned) {
                current = tier;
            }
        }
        return current;
    }
}