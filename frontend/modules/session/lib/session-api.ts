import { apiClient } from '@/lib/api-client';

import type { SessionRoom } from './schemas';

export async function createSessionRoom(bookingId: string): Promise<SessionRoom> {
  const { data } = await apiClient.post<SessionRoom>(`/sessions/${bookingId}/room`);
  return data;
}

export async function reportSessionIssue(
  bookingId: string,
  payload: { category?: string; description?: string }
): Promise<{ status: string }> {
  const { data } = await apiClient.post<{ status: string }>(
    `/sessions/${bookingId}/report-issue`,
    payload
  );
  return data;
}
