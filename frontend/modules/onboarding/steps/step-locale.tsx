'use client';

import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { TIMEZONE_OPTIONS } from '@/modules/availability/lib/schemas';

import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';
import {
  COUNTRY_OPTIONS,
  LANGUAGE_OPTIONS
} from '../lib/schemas';

export function StepLocale() {
  const draft = useOnboardingWizard((s) => s.draft);
  const update = useOnboardingWizard((s) => s.update);
  const profile = draft.profile;

  function toggleLanguage(value: string) {
    const has = profile.languages.includes(value);
    update({
      profile: {
        ...profile,
        languages: has
          ? profile.languages.filter((l) => l !== value)
          : [...profile.languages, value]
      }
    });
  }

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-xl font-semibold">Languages & country</h2>
        <p className="text-sm text-muted-foreground">
          Used to match you with compatible teachers.
        </p>
      </header>

      <div className="space-y-5 rounded-lg border bg-card p-4">
        <div className="space-y-2">
          <Label>Languages you speak</Label>
          <div className="flex flex-wrap gap-2">
            {LANGUAGE_OPTIONS.map((lang) => {
              const active = profile.languages.includes(lang.value);
              return (
                <button
                  key={lang.value}
                  type="button"
                  onClick={() => toggleLanguage(lang.value)}
                  className={`rounded-full border px-3 py-1 text-sm ${
                    active
                      ? 'border-primary bg-primary text-primary-foreground'
                      : 'hover:border-primary/40'
                  }`}
                >
                  {lang.label}
                </button>
              );
            })}
          </div>
        </div>

        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          <div className="space-y-1">
            <Label htmlFor="country">Country</Label>
            <select
              id="country"
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
              value={profile.countryCode}
              onChange={(e) =>
                update({ profile: { ...profile, countryCode: e.target.value.toUpperCase() } })
              }
            >
              <option value="">Select…</option>
              {COUNTRY_OPTIONS.map((c) => (
                <option key={c.value} value={c.value}>
                  {c.label}
                </option>
              ))}
            </select>
          </div>
          <div className="space-y-1">
            <Label htmlFor="tz">Timezone</Label>
            <select
              id="tz"
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
              value={profile.timezone}
              onChange={(e) =>
                update({ profile: { ...profile, timezone: e.target.value } })
              }
            >
              {TIMEZONE_OPTIONS.map((tz) => (
                <option key={tz} value={tz}>
                  {tz}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="space-y-1">
          <Label htmlFor="bio">Short bio (max 500 chars)</Label>
          <Input
            id="bio"
            maxLength={500}
            value={profile.bio}
            onChange={(e) => update({ profile: { ...profile, bio: e.target.value } })}
            placeholder="e.g. Backend engineer, weekend baker."
          />
          <p className="text-right text-xs text-muted-foreground">
            {profile.bio.length}/500
          </p>
        </div>
      </div>
    </div>
  );
}
