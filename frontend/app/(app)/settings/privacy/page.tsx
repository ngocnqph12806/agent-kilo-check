'use client';

import Link from 'next/link';

import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';

import { PrivacyDangerZone } from '@/modules/profile/components/privacy-danger-zone';

export default function SettingsPrivacyPage() {
  return (
    <main className="container max-w-3xl space-y-8 py-10">
      <header className="space-y-2">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Settings</p>
        <h1 className="text-3xl font-bold tracking-tight">Privacy &amp; data</h1>
        <p className="text-sm text-muted-foreground">
          Read our{' '}
          <Link href="/privacy" className="text-primary underline-offset-4 hover:underline">
            Privacy Policy
          </Link>{' '}
          and{' '}
          <Link href="/terms" className="text-primary underline-offset-4 hover:underline">
            Terms of Service
          </Link>
          .
        </p>
      </header>

      <Alert>
        <AlertTitle>You are in control</AlertTitle>
        <AlertDescription>
          SkillSeed stores the minimum data we need to run the marketplace. You can download a
          full JSON copy at any time, or close your account and have everything deleted after a
          30-day grace period.
        </AlertDescription>
      </Alert>

      <PrivacyDangerZone />
    </main>
  );
}