'use client';

import * as React from 'react';
import Link from 'next/link';
import { AlertTriangle } from 'lucide-react';

import { Button } from '@/components/ui/button';

interface ErrorPageProps {
  error: Error & { digest?: string };
  reset: () => void;
}

export default function GlobalError({ error, reset }: ErrorPageProps) {
  React.useEffect(() => {
    if (typeof window !== 'undefined' && typeof console !== 'undefined') {
      console.error('[skillseed:error-boundary]', error);
    }
  }, [error]);

  return (
    <main className="flex min-h-[80vh] flex-col items-center justify-center bg-[var(--brand-surface)] px-4">
      <div className="flex max-w-md flex-col items-center text-center">
        <div className="mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-[var(--brand-rose)]/15 shadow-brand-card">
          <AlertTriangle className="h-12 w-12 text-[var(--brand-rose)]" aria-hidden />
        </div>
        <p className="mb-2 inline-flex items-center rounded-full border border-[var(--brand-rose)]/40 bg-[var(--brand-rose)]/10 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-[var(--brand-rose)]">
          500 · Server error
        </p>
        <h1 className="text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
          Something went wrong
        </h1>
        <p className="mt-3 max-w-md text-base text-[var(--brand-text-muted)]">
          We&apos;ve been notified. Please try again in a moment — most issues clear themselves quickly.
        </p>
        {error.digest ? (
          <code className="mt-3 rounded bg-white px-2 py-1 text-xs text-[var(--brand-text-muted)] shadow-brand-card">
            Ref: {error.digest}
          </code>
        ) : null}
        <div className="mt-6 flex flex-wrap justify-center gap-3">
          <Button type="button"
            onClick={() => reset()}
            variant="brand"
            className="rounded-full"
          >
            ↻ Reload page
          </Button>
          <Button asChild variant="outline" className="rounded-full">
            <Link href="/">🏠 Back to home</Link>
          </Button>
        </div>
        <p className="mt-8 text-xs text-[var(--brand-text-subtle)]">
          If this persists, contact{' '}
          <a
            href="mailto:support@skillseed.app"
            className="font-semibold text-[var(--brand-cta-from)]"
          >
            support@skillseed.app
          </a>
        </p>
      </div>
    </main>
  );
}