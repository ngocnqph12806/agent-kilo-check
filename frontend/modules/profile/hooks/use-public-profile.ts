'use client';

import { useQuery } from '@tanstack/react-query';

import {
  getPublicProfile,
  getUserFreeSlots
} from '../lib/profile-api';

export function usePublicProfile(id: string, enabled = true) {
  return useQuery({
    queryKey: ['users', 'public', id],
    queryFn: () => getPublicProfile(id),
    enabled,
    staleTime: 60 * 1000
  });
}

export function useUserFreeSlots(id: string, days = 14, enabled = true) {
  return useQuery({
    queryKey: ['users', 'availability', id, days],
    queryFn: () => getUserFreeSlots(id, days),
    enabled,
    staleTime: 60 * 1000
  });
}
