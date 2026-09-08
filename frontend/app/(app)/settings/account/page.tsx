'use client';

import Link from 'next/link';
import { useEffect, useState } from 'react';

import { Loader2, Save } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useCurrentUser } from '@/modules/auth/hooks/use-auth-mutations';
import { patchProfile, type PatchProfilePayload } from '@/modules/onboarding/lib/onboarding-api';

export default function SettingsAccountPage() {
  const me = useCurrentUser();
  const [fullName, setFullName] = useState('');
  const [bio, setBio] = useState('');
  const [timezone, setTimezone] = useState('');
  const [saving, setSaving] = useState(false);
  const [savedAt, setSavedAt] = useState<Date | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (me.data) {
      setFullName(me.data.fullName ?? '');
      if (!timezone && typeof Intl !== 'undefined') {
        setTimezone(Intl.DateTimeFormat().resolvedOptions().timeZone);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [me.data]);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSaving(true);
    try {
      const payload: PatchProfilePayload = {
        fullName: fullName.trim(),
        bio: bio.trim() || undefined,
        timezone: timezone.trim() || undefined
      };
      await patchProfile(payload);
      setSavedAt(new Date());
      void me.refetch();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not save your changes.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="container max-w-3xl space-y-8 py-10">
      <header className="space-y-2">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Settings</p>
        <h1 className="text-3xl font-bold tracking-tight">Account</h1>
        <p className="text-sm text-muted-foreground">
          Update your profile details. Changes are reflected on bookings and discovery listings.
        </p>
      </header>

      <form onSubmit={onSubmit} className="space-y-6 rounded-2xl border bg-card p-6 shadow-sm">
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            value={me.data?.email ?? ''}
            disabled
            aria-readonly
          />
          <p className="text-xs text-muted-foreground">
            To change the email you sign in with, contact{' '}
            <Link href="/help" className="text-primary underline-offset-4 hover:underline">
              support
            </Link>
            .
          </p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="fullName">Full name</Label>
          <Input
            id="fullName"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            disabled={me.isLoading}
            required
            minLength={2}
            maxLength={80}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="bio">Short bio</Label>
          <textarea
            id="bio"
            value={bio}
            onChange={(e) => setBio(e.target.value)}
            disabled={me.isLoading}
            rows={3}
            maxLength={300}
            className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
          />
          <p className="text-xs text-muted-foreground">Up to 300 characters.</p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="timezone">Timezone</Label>
          <Input
            id="timezone"
            value={timezone}
            onChange={(e) => setTimezone(e.target.value)}
            disabled={me.isLoading}
            placeholder="e.g. America/New_York"
          />
        </div>

        {error ? (
          <p className="rounded-md border border-destructive/40 bg-destructive/10 px-3 py-2 text-sm text-destructive">
            {error}
          </p>
        ) : null}
        {savedAt ? (
          <p className="text-xs text-emerald-600">
            Saved {savedAt.toLocaleTimeString()}.
          </p>
        ) : null}

        <div className="flex justify-end">
          <Button type="submit" variant="brand" disabled={saving || me.isLoading}>
            {saving ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" aria-hidden />
                Saving…
              </>
            ) : (
              <>
                <Save className="mr-2 h-4 w-4" aria-hidden />
                Save changes
              </>
            )}
          </Button>
        </div>
      </form>

      <section className="space-y-3 rounded-2xl border border-destructive/40 bg-destructive/5 p-6">
        <h2 className="text-lg font-semibold">Close your account</h2>
        <p className="text-sm text-muted-foreground">
          Account deletion lives in{' '}
          <Link href="/settings/privacy" className="font-semibold text-primary underline-offset-4 hover:underline">
            Privacy &amp; data
          </Link>
          , where you can also export a copy of your data first.
        </p>
      </section>
    </main>
  );
}
