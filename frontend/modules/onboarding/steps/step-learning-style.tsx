'use client';

import { LEARNING_STYLES } from '../lib/schemas';
import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';

export function StepLearningStyle() {
  const draft = useOnboardingWizard((s) => s.draft);
  const update = useOnboardingWizard((s) => s.update);
  const profile = draft.profile;

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-xl font-semibold">Learning style</h2>
        <p className="text-sm text-muted-foreground">
          Helps teachers tailor their sessions.
        </p>
      </header>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {LEARNING_STYLES.map((style) => (
          <button
            key={style.value}
            type="button"
            onClick={() =>
              update({ profile: { ...profile, learningStyle: style.value } })
            }
            className={`rounded-lg border p-4 text-left transition ${
              profile.learningStyle === style.value
                ? 'border-primary bg-primary/5'
                : 'hover:border-primary/40'
            }`}
          >
            <p className="font-medium">{style.label}</p>
            <p className="text-xs text-muted-foreground">{descriptionFor(style.value)}</p>
          </button>
        ))}
      </div>
    </div>
  );
}

function descriptionFor(value: string): string {
  switch (value) {
    case 'visual':
      return 'Diagrams, screen-share, videos.';
    case 'auditory':
      return 'Verbal explanation, live dialogue.';
    case 'reading':
      return 'Articles, docs, written notes.';
    case 'kinesthetic':
      return 'Hands-on projects, building together.';
    default:
      return '';
  }
}
