import { apiClient } from '@/lib/api-client';

export interface DataExportProfile {
  id: string;
  email: string | null;
  phone: string | null;
  fullName: string | null;
  avatarUrl: string | null;
  bio: string | null;
  countryCode: string | null;
  timezone: string | null;
  languages: string[];
  learningStyle: string | null;
  authProvider: string;
  verified: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DataExportBooking {
  id: string;
  otherPartyId: string | null;
  otherPartyName: string | null;
  status: string;
  scheduledAt: string;
  durationMinutes: number;
  createdAt: string;
}

export interface DataExportRating {
  bookingId: string | null;
  raterId: string | null;
  rateeId: string | null;
  overallScore: number | null;
  comment: string | null;
  autoRated: boolean;
  createdAt: string;
}

export interface DataExportSeedTransaction {
  type: string;
  amount: number;
  balanceAfter: number;
  description: string | null;
  status: string;
  createdAt: string;
  expiresAt: string | null;
}

export interface DataExportResponse {
  exportedAt: string;
  profile: DataExportProfile;
  offeredSkills: unknown[];
  wantedSkills: unknown[];
  availability: unknown[];
  wallet: { balance: number; totalEarned: number; totalSpent: number };
  seedTransactions: DataExportSeedTransaction[];
  bookingsAsTeacher: DataExportBooking[];
  bookingsAsLearner: DataExportBooking[];
  ratingsGiven: DataExportRating[];
  ratingsReceived: DataExportRating[];
}

export async function exportMyData(): Promise<DataExportResponse> {
  const { data } = await apiClient.get<DataExportResponse>('/users/me/export');
  return data;
}

export async function deleteMyAccount(): Promise<void> {
  await apiClient.delete('/users/me');
}

export function downloadExportAsFile(payload: DataExportResponse): void {
  const blob = new Blob([JSON.stringify(payload, null, 2)], {
    type: 'application/json'
  });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  const stamp = new Date().toISOString().replace(/[:.]/g, '-');
  link.download = `skillseed-data-export-${stamp}.json`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}