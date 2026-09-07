'use client';

import { useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import {
  TOTAL_STEPS,
  useOnboardingWizard
} from '../hooks/use-onboarding-wizard';
import { useSubmitOnboarding } from '../hooks/use-submit-onboarding';
import { StepAvailability } from '../steps/step-availability';
import { StepGoals } from '../steps/step-goals';
import { StepLearningStyle } from '../steps/step-learning-style';
import { StepLocale } from '../steps/step-locale';
import { StepOfferedSkills } from '../steps/step-offered-skills';
import { StepWantedSkills } from '../steps/step-wanted-skills';

const STEPS = [
  { label: 'Skills I teach', Component: StepOfferedSkills },
  { label: 'Skills I want', Component: StepWantedSkills },
  { label: 'Goals', Component: StepGoals },
  { label: 'Learning style', Component: StepLearningStyle },
  { label: 'Availability', Component: StepAvailability },
  { label: 'Languages & country', Component: StepLocale }
] as const;

export function OnboardingWizard() {
  const step = useOnboardingWizard((s) => s.step);
  const next = useOnboardingWizard((s) => s.next);
  const back = useOnboardingWizard((s) => s.back);
  const draft = useOnboardingWizard((s) => s.draft);
  const submit = useSubmitOnboarding();
  const [serverError, setServerError] = useState<string | null>(null);

  const ActiveStep = STEPS[step].Component;
  const isFinal = step === STEPS.length - 1;
  const canAdvance = validateStep(step, draft);

  function onNext() {
    if (isFinal) {
      submit.mutate(draft, {
        onError: (err) =>
          setServerError(
            err instanceof Error ? err.message : 'Failed to complete onboarding.'
          )
      });
    } else {
      next();
    }
  }

  return (
    <main className="container mx-auto max-w-2xl py-10">
      <header className="mb-8 space-y-2 text-center">
        <h1 className="text-3xl font-bold tracking-tight">
          Set up your SkillSeed profile
        </h1>
        <p className="text-sm text-muted-foreground">
          Step {step + 1} of {TOTAL_STEPS} · {STEPS[step].label}
        </p>
      </header>

      <ol className="mb-8 flex items-center gap-1" aria-label="progress">
        {STEPS.map((s, idx) => (
          <li
            key={s.label}
            className={cn(
              'h-1.5 flex-1 rounded-full',
              idx <= step ? 'bg-primary' : 'bg-muted'
            )}
            aria-current={idx === step ? 'step' : undefined}
          />
        ))}
      </ol>

      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <ActiveStep />

        {serverError ? (
          <p className="mt-4 rounded-md border border-destructive/30 bg-destructive/5 p-3 text-sm text-destructive">
            {serverError}
          </p>
        ) : null}

        <div className="mt-8 flex items-center justify-between border-t pt-4">
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
          >
            {submit.isPending
              ? 'Finishing…'
              : isFinal
                ? 'Finish & claim seeds'
                : 'Next'}
          </Button>
        </div>
        {!canAdvance ? (
          <p className="mt-3 text-center text-xs text-muted-foreground">
            {stepHint(step)}
          </p>
        ) : null}
      </div>
    </main>
  );
}

function validateStep(step: number, draft: ReturnType<typeof useOnboardingWizard.getState>['draft']): boolean {
  switch (step) {
    case 0:
      return draft.offered.length > 0;
    case 1:
      return draft.wanted.length > 0;
    case 2:
      return draft.profile.goals.trim().length >= 10;
    case 3:
      return Boolean(draft.profile.learningStyle);
    case 4:
      return true;
    case 5:
      return draft.profile.languages.length > 0 && draft.profile.countryCode.length === 2;
    default:
      return true;
  }
}

function stepHint(step: number): string {
  switch (step) {
    case 0:
      return 'Add at least one skill you can teach to continue.';
    case 1:
      return 'Add at least one skill you want to learn.';
    case 2:
      return 'Tell us a bit about your goals (10+ chars).';
    case 3:
      return 'Pick a learning style.';
    case 5:
      return 'Pick at least one language and your 2-letter country code.';
    default:
      return '';
  }
}
