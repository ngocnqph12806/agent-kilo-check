'use client';

import { create } from 'zustand';
import { persist } from 'zustand/middleware';

import { EMPTY_DRAFT, type OnboardingDraft } from '../lib/schemas';

interface WizardState {
  step: number;
  draft: OnboardingDraft;
  setStep: (step: number) => void;
  next: () => void;
  back: () => void;
  update: (partial: Partial<OnboardingDraft>) => void;
  reset: () => void;
}

export const TOTAL_STEPS = 8;

export const useOnboardingWizard = create<WizardState>()(
  persist(
    (set) => ({
      step: 0,
      draft: EMPTY_DRAFT,
      setStep: (step) => set({ step: Math.max(0, Math.min(step, TOTAL_STEPS - 1)) }),
      next: () =>
        set((state) => ({
          step: Math.min(state.step + 1, TOTAL_STEPS - 1)
        })),
      back: () => set((state) => ({ step: Math.max(state.step - 1, 0) })),
      update: (partial) =>
        set((state) => ({ draft: { ...state.draft, ...partial } })),
      reset: () => set({ step: 0, draft: EMPTY_DRAFT })
    }),
    {
      name: 'skillseed.onboarding-draft',
      partialize: (state) => ({ draft: state.draft, step: state.step })
    }
  )
);
