'use client';

import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { PartyPopper, CheckCircle2 } from 'lucide-react';

import { Button } from '@/components/ui/button';

export function StepDone() {
  const router = useRouter();

  return (
    <div className="flex flex-col items-center px-4 py-12 text-center">
      <div className="mb-4 inline-flex h-16 w-16 items-center justify-center rounded-full bg-[var(--brand-hero-soft)] text-primary shadow-brand-card">
        <PartyPopper className="h-9 w-9" aria-hidden />
      </div>
      <h1 className="text-4xl font-extrabold tracking-tight text-[var(--brand-text-strong)] sm:text-5xl">
        You&rsquo;re all set! 🎉
      </h1>
      <p className="mt-4 max-w-xl text-base text-[var(--brand-text-muted)] sm:text-lg">
        Your profile is ready. We&rsquo;ve already matched you with{' '}
        <span className="font-semibold text-primary">12 amazing learning partners</span>{' '}
        waiting to chat.
      </p>

      <ul className="mt-10 grid w-full max-w-md gap-3 text-left">
        {['Profile created', 'Skills seeded for matching', '50 starter seeds claimed', 'Email verified'].map(
          (item) => (
            <li
              key={item}
              className="flex items-center gap-3 rounded-xl border border-[var(--brand-border)] bg-card p-3 text-sm shadow-brand-card"
            >
              <CheckCircle2 className="h-5 w-5 shrink-0 text-primary" aria-hidden />
              <span className="font-medium text-[var(--brand-text-strong)]">{item}</span>
            </li>
          )
        )}
      </ul>

      <div className="mt-10 flex flex-col gap-3 sm:flex-row">
        <Button
          type="button"
          variant="brand"
          size="lg"
          onClick={() => router.replace('/discover')}
          className="h-12 rounded-full px-10 text-base font-semibold"
        >
          Meet your matches →
        </Button>
        <Button
          asChild
          variant="outline"
          size="lg"
          className="h-12 rounded-full px-10 text-base"
        >
          <Link href="/wallet">View my wallet</Link>
        </Button>
      </div>
    </div>
  );
}
