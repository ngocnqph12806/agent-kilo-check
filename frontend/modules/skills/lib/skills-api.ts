import { apiClient } from '@/lib/api-client';

import type { Skill, SkillCategory, SkillPage } from './schemas';

export interface SkillSearchParams {
  query?: string;
  category?: SkillCategory;
  page?: number;
  size?: number;
}

export async function searchSkills(params: SkillSearchParams): Promise<SkillPage> {
  const { data } = await apiClient.get<SkillPage>('/skills', { params });
  return data;
}

export async function getSkill(id: string): Promise<Skill> {
  const { data } = await apiClient.get<Skill>(`/skills/${id}`);
  return data;
}

export interface CreateCustomSkillInput {
  name: string;
  category: SkillCategory;
  parentId?: string;
}

export async function createCustomSkill(input: CreateCustomSkillInput): Promise<Skill> {
  const { data } = await apiClient.post<Skill>('/skills', input);
  return data;
}
