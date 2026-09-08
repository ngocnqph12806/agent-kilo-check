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

export type ReportIssueCategory =
  | 'audio'
  | 'video'
  | 'connection'
  | 'screen_share'
  | 'chat'
  | 'other';

export interface ReportIssueInput {
  category: ReportIssueCategory;
  description: string;
}

export interface ReportIssueResponse {
  incidentId: string;
}

export async function reportSessionIssue(
  bookingId: string,
  input: ReportIssueInput
): Promise<ReportIssueResponse> {
  const { data } = await apiClient.post<ReportIssueResponse>(
    `/sessions/${bookingId}/report-issue`,
    input
  );
  return data;
}
