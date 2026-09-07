'use client';

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';

import {
  completeOnboarding,
  createOfferedSkill,
  createWantedSkill,
  deleteOfferedSkill,
  deleteWantedSkill,
  patchProfile,
  replaceAvailability
} from '../lib/onboarding-api';
import type { OnboardingDraft } from '../lib/schemas';

import { useOnboardingWizard } from './use-onboarding-wizard';

export function useSubmitOnboarding() {
  const router = useRouter();
  const qc = useQueryClient();
  const reset = useOnboardingWizard((s) => s.reset);
  const draft = useOnboardingWizard((s) => s.draft);

  return useMutation({
    mutationFn: async (input: OnboardingDraft) => {
      if (input.profile.fullName) {
        await patchProfile({
          fullName: input.profile.fullName,
          bio: input.profile.bio,
          countryCode: input.profile.countryCode,
          timezone: input.profile.timezone,
          languages: input.profile.languages,
          learningStyle: input.profile.learningStyle || undefined
        });
      }

      for (const item of input.offered) {
        await createOfferedSkill({
          skillId: item.skill.id,
          level: item.level,
          yearsExperience: item.yearsExperience,
          description: item.description,
          hourlySeedRate: item.hourlySeedRate
        });
      }

      for (const item of input.wanted) {
        await createWantedSkill({
          skillId: item.skill.id,
          priority: item.priority,
          targetLevel: item.targetLevel,
          notes: item.notes
        });
      }

      if (input.availability.slots.length > 0) {
        await replaceAvailability(input.availability);
      }

      return completeOnboarding();
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['auth', 'me'] });
      qc.invalidateQueries({ queryKey: ['users', 'me'] });
      reset();
      router.replace('/discover?welcome=1');
    }
  });
}

export function useRemoveOfferedDraft() {
  const update = useOnboardingWizard((s) => s.update);
  const draft = useOnboardingWizard.getState().draft;
  return (skillId: string) => {
    update({ offered: draft.offered.filter((o) => o.skill.id !== skillId) });
  };
}

export function useRemoveWantedDraft() {
  const update = useOnboardingWizard((s) => s.update);
  const draft = useOnboardingWizard.getState().draft;
  return (skillId: string) => {
    update({ wanted: draft.wanted.filter((w) => w.skill.id !== skillId) });
  };
}

export function useDeletePersistedOffered() {
  return useMutation({
    mutationFn: (id: string) => deleteOfferedSkill(id)
  });
}

export function useDeletePersistedWanted() {
  return useMutation({
    mutationFn: (id: string) => deleteWantedSkill(id)
  });
}
