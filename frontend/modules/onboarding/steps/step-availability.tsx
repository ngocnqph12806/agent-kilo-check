'use client';

import { AvailabilityPicker } from '@/modules/availability/components/availability-picker';

import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';

export function StepAvailability() {
  const draft = useOnboardingWizard((s) => s.draft);
  const update = useOnboardingWizard((s) => s.update);

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-xl font-semibold">Weekly availability</h2>
        <p className="text-sm text-muted-foreground">
          Pick a recurring window for each day. You can fine-tune later.
        </p>
      </header>

      <AvailabilityPicker
        value={draft.availability}
        onChange={(availability) => update({ availability })}
      />
    </div>
  );
}
