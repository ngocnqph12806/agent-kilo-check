import { apiClient } from '@/lib/api-client';

export interface SessionRoom {
  roomUrl: string;
  roomName: string;
  token: string;
  role: 'owner' | 'participant';
  expiresAt: string;
  scheduledAt: string;
  durationMinutes: number;
}

export async function openSessionRoom(bookingId: string): Promise<SessionRoom> {
  const { data } = await apiClient.post<SessionRoom>(`/sessions/${bookingId}/room`);
  return data;
}
