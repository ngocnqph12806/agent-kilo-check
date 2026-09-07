import { apiClient } from '@/lib/api-client';

import type { PublicUserProfile } from './schemas';

export async function getPublicProfile(id: string): Promise<PublicUserProfile> {
  const { data } = await apiClient.get<PublicUserProfile>(`/users/${id}`);
  return data;
}

export interface FreeSlot {
  startsAt: string;
  endsAt: string;
  timezone: string;
  sourceDow: number;
}

export async function getUserFreeSlots(
  id: string,
  days = 14
): Promise<FreeSlot[]> {
  const { data } = await apiClient.get<FreeSlot[]>(`/users/${id}/availability`, {
    params: { days }
  });
  return data;
}
