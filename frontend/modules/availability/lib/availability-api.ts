import { apiClient } from '@/lib/api-client';

import type { AvailabilityState } from './schemas';

export async function listMyAvailability(): Promise<AvailabilityState> {
  const { data } = await apiClient.get<AvailabilityState>('/users/me/availability');
  return data;
}

export async function replaceMyAvailability(
  state: AvailabilityState
): Promise<AvailabilityState> {
  const { data } = await apiClient.put<AvailabilityState>('/users/me/availability', {
    slots: state.slots,
    timezone: state.timezone
  });
  return data;
}
