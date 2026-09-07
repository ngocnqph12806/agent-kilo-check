'use client';

import { useQuery } from '@tanstack/react-query';

import { searchSkills, type SkillSearchParams } from '../lib/skills-api';

const SKILLS_KEY = ['skills', 'search'] as const;

export function useSkillSearch(params: SkillSearchParams, enabled = true) {
  return useQuery({
    queryKey: [...SKILLS_KEY, params],
    queryFn: () => searchSkills(params),
    enabled,
    staleTime: 30 * 1000
  });
}
