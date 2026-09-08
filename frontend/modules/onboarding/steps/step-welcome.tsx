'use client';

import { Sparkles, Users, Wallet } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';

export function StepWelcome() {
  const next = useOnboardingWizard((s) => s.next);

  return (
    <div className="flex flex-col items-center px-4 py-12 text-center">
      <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-brand-hero-soft text-4xl shadow-brand-card">
        <Sparkles className="h-9 w-9 text-primary" aria-hidden />
      </div>
      <h1 className="text-4xl font-extrabold tracking-tight text-[var(--brand-text-strong)] sm:text-5xl">
        Welcome to SkillSeed!
      </h1>
      <p className="mt-4 max-w-xl text-base text-[var(--brand-text-muted)] sm:text-lg">
        Let&rsquo;s set up your profile in 7 quick steps.
        <br />
        It takes about 3 minutes.
      </p>

      <ul className="mt-10 grid w-full max-w-2xl gap-4 text-left sm:grid-cols-3">
        <li className="rounded-2xl border border-[var(--brand-border)] bg-card p-5 shadow-brand-card">
          <Users className="mb-3 h-6 w-6 text-primary" aria-hidden />
          <p className="font-semibold text-[var(--brand-text-strong)]">Match with 12+ partners</p>
          <p className="mt-1 text-sm text-[var(--brand-text-muted)]">
            We suggest people who want what you teach and offer what you want to learn.
          </p>
        </li>
        <li className="rounded-2xl border border-[var(--brand-border)] bg-card p-5 shadow-brand-card">
          <Wallet className="mb-3 h-6 w-6 text-primary" aria-hidden />
          <p className="font-semibold text-[var(--brand-text-strong)]">Claim 50 starter seeds</p>
          <p className="mt-1 text-sm text-[var(--brand-text-muted)]">
            Use them to book sessions. Earn more by teaching.
          </p>
        </li>
        <li className="rounded-2xl border border-[var(--brand-border)] bg-card p-5 shadow-brand-card">
          <Sparkles className="mb-3 h-6 w-6 text-primary" aria-hidden />
          <p className="font-semibold text-[var(--brand-text-strong)]">No money changes hands</p>
          <p className="mt-1 text-sm text-[var(--brand-text-muted)]">
            Just skills-for-skills, in a global learning community.
          </p>
        </li>
      </ul>

      <Button
        type="button"
        variant="brand"
        size="lg"
        onClick={next}
        className="mt-10 h-12 rounded-full px-10 text-base font-semibold"
      >
        Let&rsquo;s go →
      </Button>
    </div>
  );
}
