'use client';

import { Check } from 'lucide-react';

import { Input } from '@/components/ui/input';
import { cn } from '@/lib/utils';
import {
  LEARNING_STYLES,
  PRIMARY_GOALS,
  WEEKLY_COMMITMENTS
} from '../lib/schemas';
import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';

export function StepGoalsStyle() {
  const draft = useOnboardingWizard((s) => s.draft);
  const update = useOnboardingWizard((s) => s.update);
  const profile = draft.profile;

  return (
    <div className="space-y-8">
      <section className="space-y-3">
        <h3 className="text-sm font-bold uppercase tracking-wide text-brand-muted">
          What&rsquo;s your primary goal?
        </h3>
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
          {PRIMARY_GOALS.map((goal) => {
            const active = profile.primaryGoal === goal.value;
            return (
              <button
                key={goal.value}
                type="button"
                onClick={() => update({ profile: { ...profile, primaryGoal: goal.value } })}
                className={cn(
                  'relative flex h-24 flex-col items-start justify-between rounded-xl border bg-card p-4 text-left transition',
                  active
                    ? 'border-primary ring-2 ring-primary/40'
                    : 'border-brand-default hover:border-primary/40'
                )}
              >
                <span className="text-2xl" aria-hidden>
                  {goal.emoji}
                </span>
                <div>
                  <p className="text-sm font-semibold text-brand-strong">{goal.label}</p>
                  {active ? (
                    <p className="mt-1 inline-flex items-center gap-1 text-[11px] font-semibold text-primary">
                      <Check className="h-3 w-3" /> Selected
                    </p>
                  ) : null}
                </div>
              </button>
            );
          })}
        </div>
      </section>

      <section className="space-y-3">
        <h3 className="text-sm font-bold uppercase tracking-wide text-brand-muted">
          How much time can you commit weekly?
        </h3>
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
          {WEEKLY_COMMITMENTS.map((slot) => {
            const active = profile.weeklyCommitment === slot.value;
            return (
              <button
                key={slot.value}
                type="button"
                onClick={() => update({ profile: { ...profile, weeklyCommitment: slot.value } })}
                className={cn(
                  'rounded-xl border bg-card p-3 text-left transition',
                  active
                    ? 'border-primary ring-2 ring-primary/40'
                    : 'border-brand-default hover:border-primary/40'
                )}
              >
                <p className="text-sm font-semibold text-brand-strong">{slot.label}</p>
                <p className={cn('mt-0.5 text-xs', active ? 'text-primary' : 'text-brand-muted')}>
                  {active ? '✓ ' : ''}
                  {slot.sub}
                </p>
              </button>
            );
          })}
        </div>
      </section>

      <section className="space-y-3">
        <h3 className="text-sm font-bold uppercase tracking-wide text-brand-muted">
          Preferred learning style (pick all that apply)
        </h3>
        <div className="flex flex-wrap gap-2">
          {LEARNING_STYLES.map((style) => {
            const active = profile.learningStyle === style.value;
            return (
              <button
                key={style.value}
                type="button"
                onClick={() => update({ profile: { ...profile, learningStyle: style.value } })}
                className={cn(
                  'inline-flex h-11 items-center gap-2 rounded-full border px-4 text-sm font-medium transition',
                  active
                    ? 'border-transparent bg-brand-cta text-white shadow-brand-cta'
                    : 'border-brand-default bg-card text-brand-strong hover:border-primary/40'
                )}
              >
                <span aria-hidden>{style.emoji}</span>
                <span>{style.label}</span>
                {active ? <Check className="h-4 w-4" /> : null}
              </button>
            );
          })}
        </div>
      </section>

      <section className="space-y-3 rounded-xl border border-brand-default bg-card p-4">
        <h3 className="text-sm font-bold uppercase tracking-wide text-brand-muted">
          Goals &amp; interests
        </h3>
        <div className="space-y-2">
          <label htmlFor="goals" className="text-sm font-medium">
            What do you want to achieve on SkillSeed?
          </label>
          <textarea
            id="goals"
            rows={4}
            className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            value={profile.goals}
            onChange={(e) => update({ profile: { ...profile, goals: e.target.value } })}
            placeholder="e.g. Master Vietnamese cooking and help 3 beginners each month."
          />
        </div>
        <div className="space-y-2">
          <label htmlFor="interests" className="text-sm font-medium">
            Other interests (comma separated)
          </label>
          <Input
            id="interests"
            value={profile.interests}
            onChange={(e) => update({ profile: { ...profile, interests: e.target.value } })}
            placeholder="indie games, sourdough, sci-fi novels…"
          />
        </div>
      </section>
    </div>
  );
}
