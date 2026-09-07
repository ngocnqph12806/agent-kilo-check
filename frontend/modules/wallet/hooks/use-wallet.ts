'use client';

import { useQuery } from '@tanstack/react-query';

import {
  getWalletSummary,
  getWalletTransactions
} from '../lib/wallet-api';

const SUMMARY_KEY = ['wallet', 'me'] as const;
const TX_KEY = ['wallet', 'transactions'] as const;

export function useWalletSummary(enabled = true) {
  return useQuery({
    queryKey: SUMMARY_KEY,
    queryFn: getWalletSummary,
    enabled,
    staleTime: 30 * 1000,
    refetchInterval: 60 * 1000
  });
}

export function useWalletTransactions(page = 0, size = 20, enabled = true) {
  return useQuery({
    queryKey: [...TX_KEY, page, size],
    queryFn: () => getWalletTransactions(page, size),
    enabled,
    staleTime: 30 * 1000
  });
}
