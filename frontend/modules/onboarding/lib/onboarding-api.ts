import { apiClient } from '@/lib/api-client';

export interface OfferedSkillPayload {
  skillId: string;
  level: number;
  yearsExperience?: number;
  description?: string;
  hourlySeedRate: number;
}

export interface WantedSkillPayload {
  skillId: string;
  priority: number;
  targetLevel: number;
  notes?: string;
}

export interface AvailabilityPayload {
  slots: Array<{
    dayOfWeek: number;
    startTime: string;
    endTime: string;
    timezone: string;
  }>;
  timezone: string;
}

export interface OnboardingCompleteResponse {
  onboardingCompleted: boolean;
  starterSeedsGranted: number;
  walletBalance: number;
}

export async function createOfferedSkill(
  payload: OfferedSkillPayload
): Promise<{ id: string }> {
  const { data } = await apiClient.post<{ id: string }>(
    '/users/me/skills/offered',
    payload
  );
  return data;
}

export async function deleteOfferedSkill(id: string): Promise<void> {
  await apiClient.delete(`/users/me/skills/offered/${id}`);
}

export async function createWantedSkill(
  payload: WantedSkillPayload
): Promise<{ id: string }> {
  const { data } = await apiClient.post<{ id: string }>(
    '/users/me/skills/wanted',
    payload
  );
  return data;
}

export async function deleteWantedSkill(id: string): Promise<void> {
  await apiClient.delete(`/users/me/skills/wanted/${id}`);
}

export async function replaceAvailability(payload: AvailabilityPayload) {
  const { data } = await apiClient.put('/users/me/availability', payload);
  return data;
}

export interface PatchProfilePayload {
  fullName?: string;
  bio?: string;
  countryCode?: string;
  timezone?: string;
  languages?: string[];
  learningStyle?: string;
}

export async function patchProfile(payload: PatchProfilePayload) {
  const { data } = await apiClient.patch('/users/me', payload);
  return data;
}

export async function completeOnboarding(): Promise<OnboardingCompleteResponse> {
  const { data } = await apiClient.post<OnboardingCompleteResponse>(
    '/users/me/onboarding',
    {}
  );
  return data;
}
