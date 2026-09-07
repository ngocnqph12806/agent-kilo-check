import { apiClient } from '@/lib/api-client';

import type { SeedTransactionPage, WalletSummary } from './schemas';

export async function getWalletSummary(): Promise<WalletSummary> {
  const { data } = await apiClient.get<WalletSummary>('/wallet/me');
  return data;
}

export async function getWalletTransactions(
  page = 0,
  size = 20
): Promise<SeedTransactionPage> {
  const { data } = await apiClient.get<SeedTransactionPage>(
    '/wallet/me/transactions',
    { params: { page, size } }
  );
  return data;
}
