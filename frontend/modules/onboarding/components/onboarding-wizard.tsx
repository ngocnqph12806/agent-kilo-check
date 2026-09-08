'use client';

import { useState } from 'react';

import { Button } from '@/components/ui/button';

import {
  TOTAL_STEPS,
  useOnboardingWizard
} from '../hooks/use-onboarding-wizard';
import { useSubmitOnboarding } from '../hooks/use-submit-onboarding';
import { StepAvailability } from '../steps/step-availability';
import { StepAvatar } from '../steps/step-avatar';
import { StepDone } from '../steps/step-done';
import { StepGoalsStyle } from '../steps/step-goals-style';
import { StepLocale } from '../steps/step-locale';
import { StepOfferedSkills } from '../steps/step-offered-skills';
import { StepWantedSkills } from '../steps/step-wanted-skills';
import { StepWelcome } from '../steps/step-welcome';

const STEPS = [
  { label: 'Welcome', Component: StepWelcome },
  { label: 'Skills I teach', Component: StepOfferedSkills },
  { label: 'Skills I want', Component: StepWantedSkills },
  { label: 'Goals & style', Component: StepGoalsStyle },
  { label: 'Availability', Component: StepAvailability },
  { label: 'Languages & country', Component: StepLocale },
  { label: 'Profile photo', Component: StepAvatar },
  { label: 'Done', Component: StepDone }
] as const;

const SPLASH_STEPS = new Set<number>([0, STEPS.length - 1]);

export function OnboardingWizard() {
  const step = useOnboardingWizard((s) => s.step);
  const next = useOnboardingWizard((s) => s.next);
  const back = useOnboardingWizard((s) => s.back);
  const draft = useOnboardingWizard((s) => s.draft);
  const submit = useSubmitOnboarding();
  const [serverError, setServerError] = useState<string | null>(null);

  const ActiveStep = STEPS[step].Component;
  const isSplash = SPLASH_STEPS.has(step);
  const isFinal = step === STEPS.length - 2; // avatar is last form step before done
  const isWelcome = step === 0;
  const canAdvance = validateStep(step, draft);
  const progress = Math.round(((step + 1) / TOTAL_STEPS) * 100);

  function onNext() {
    if (isFinal) {
      submit.mutate(draft, {
        onSuccess: () => next(),
        onError: (err) =>
          setServerError(
            err instanceof Error ? err.message : 'Failed to complete onboarding.'
          )
      });
    } else {
      next();
    }
  }

  if (isSplash) {
    return (
      <main className="min-h-screen bg-[var(--brand-surface)]">
        <div className="mx-auto max-w-3xl px-4 py-6 sm:py-10">
          {isWelcome ? <ProgressHeader step={step + 1} progress={5} /> : null}
          <ActiveStep />
          {isWelcome ? (
            <p className="mt-8 text-center text-xs text-[var(--brand-text-subtle)]">
              You can update any of this later from your profile settings.
            </p>
          ) : null}
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-[var(--brand-surface)]">
      <div className="mx-auto max-w-3xl px-4 py-6 sm:py-10">
        <ProgressHeader step={step + 1} progress={progress} />

        <div className="rounded-2xl border border-[var(--brand-border)] bg-white p-6 shadow-brand-card sm:p-8">
          <ActiveStep />

          {serverError ? (
            <p className="mt-4 rounded-md border border-destructive/30 bg-destructive/5 p-3 text-sm text-destructive">
              {serverError}
            </p>
          ) : null}

          <div className="mt-8 flex items-center justify-between border-t border-[var(--brand-divider)] pt-4">
            <Button
              variant="ghost"
              type="button"
              onClick={back}
              disabled={step === 0 || submit.isPending}
            >
              Back
            </Button>
            <Button
              type="button"
              onClick={onNext}
              disabled={!canAdvance || submit.isPending}
              className="h-11 rounded-full bg-brand-cta px-8 font-semibold text-white shadow-brand-cta hover:opacity-95"
            >
              {submit.isPending
                ? 'Finishing…'
                : isFinal
                  ? 'Finish & claim seeds'
                  : 'Next →'}
            </Button>
          </div>
          {!canAdvance ? (
            <p className="mt-3 text-center text-xs text-[var(--brand-text-muted)]">{stepHint(step)}</p>
          ) : null}
        </div>
      </div>
    </main>
  );
}

function ProgressHeader({ step, progress }: { step: number; progress: number }) {
  return (
    <div className="mb-6 space-y-3">
      <div className="flex items-center justify-between text-sm">
        <span className="font-semibold text-[var(--brand-text-strong)]">SkillSeed</span>
        <span className="flex items-center gap-3 text-[var(--brand-text-muted)]">
          <span>Step {step} of {TOTAL_STEPS}</span>
          <span className="font-semibold text-primary">{progress}% complete</span>
        </span>
      </div>
      <div className="h-1.5 w-full overflow-hidden rounded-full bg-[var(--brand-border)]">
        <div
          className="h-full rounded-full bg-brand-cta transition-all"
          style={{ width: `${progress}%` }}
        />
      </div>
    </div>
  );
}

function validateStep(step: number, draft: ReturnType<typeof useOnboardingWizard.getState>['draft']): boolean {
  switch (step) {
    case 0:
      return true;
    case 1:
      return draft.offered.length > 0;
    case 2:
      return draft.wanted.length > 0;
    case 3:
      return (
        Boolean(draft.profile.primaryGoal) &&
        Boolean(draft.profile.weeklyCommitment) &&
        Boolean(draft.profile.learningStyle) &&
        draft.profile.goals.trim().length >= 10
      );
    case 4:
      return true;
    case 5:
      return draft.profile.languages.length > 0 && draft.profile.countryCode.length === 2;
    case 6:
      return true;
    default:
      return true;
  }
}

function stepHint(step: number): string {
  switch (step) {
    case 1:
      return 'Add at least one skill you can teach to continue.';
    case 2:
      return 'Add at least one skill you want to learn.';
    case 3:
      return 'Pick a goal, weekly time, learning style and add at least 10 characters about your goals.';
    case 5:
      return 'Pick at least one language and your 2-letter country code.';
    default:
      return '';
  }
}
