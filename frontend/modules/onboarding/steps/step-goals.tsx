'use client';

import { Input } from '@/components/ui/input';

import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';

export function StepGoals() {
  const draft = useOnboardingWizard((s) => s.draft);
  const update = useOnboardingWizard((s) => s.update);
  const profile = draft.profile;

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-xl font-semibold">Goals & interests</h2>
        <p className="text-sm text-muted-foreground">
          One paragraph each. We use this to seed your profile bio.
        </p>
      </header>

      <div className="space-y-4 rounded-lg border bg-card p-4">
        <div className="space-y-1">
          <label htmlFor="goals" className="text-sm font-medium">
            What do you want to achieve on SkillSeed?
          </label>
          <textarea
            id="goals"
            rows={4}
            className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            value={profile.goals}
            onChange={(e) =>
              update({ profile: { ...profile, goals: e.target.value } })
            }
            placeholder="e.g. Master Vietnamese cooking and help 3 beginners each month."
          />
        </div>
        <div className="space-y-1">
          <label htmlFor="interests" className="text-sm font-medium">
            Other interests (comma separated)
          </label>
          <Input
            id="interests"
            value={profile.interests}
            onChange={(e) =>
              update({ profile: { ...profile, interests: e.target.value } })
            }
            placeholder="indie games, sourdough, sci-fi novels…"
          />
        </div>
      </div>
    </div>
  );
}
