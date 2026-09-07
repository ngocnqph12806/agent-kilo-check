export type SeedTransactionType =
  | 'earn'
  | 'spend'
  | 'grant'
  | 'expire'
  | 'refund';

export type SeedTransactionStatus = 'pending' | 'completed' | 'cancelled';

export interface SeedTransaction {
  id: string;
  type: SeedTransactionType;
  amount: number;
  balanceAfter: number;
  status: SeedTransactionStatus;
  bookingId?: string | null;
  description?: string | null;
  expiresAt?: string | null;
  createdAt: string;
}

export interface SeedTransactionPage {
  content: SeedTransaction[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ExpiringSoon {
  amount: number;
  oldestExpiresAt?: string | null;
}

export interface WalletSummary {
  balance: number;
  totalEarned: number;
  totalSpent: number;
  totalExpired: number;
  expiringSoon: ExpiringSoon;
  tier: 'bronze' | 'silver' | 'gold' | 'platinum';
}

export const WALLET_TIER_LABEL: Record<WalletSummary['tier'], string> = {
  bronze: 'Bronze',
  silver: 'Silver',
  gold: 'Gold',
  platinum: 'Platinum'
};
