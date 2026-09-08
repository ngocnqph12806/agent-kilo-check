'use client';

import * as React from 'react';
import Link from 'next/link';

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
    <main className="container flex min-h-screen flex-col items-center justify-center gap-6 text-center">
      <span className="rounded-full border border-destructive/40 bg-destructive/10 px-3 py-1 text-xs font-medium uppercase tracking-wide text-destructive">
        500 · Server error
      </span>
      <h1 className="text-balance text-4xl font-bold tracking-tight">Something went sideways.</h1>
      <p className="max-w-md text-balance text-muted-foreground">
        We&apos;ve been notified. You can try again, or come back in a few minutes — most issues
        clear themselves quickly.
      </p>
      {error.digest && (
        <code className="rounded bg-muted px-2 py-1 text-xs text-muted-foreground">
          Ref: {error.digest}
        </code>
      )}
      <div className="flex gap-3">
        <Button onClick={() => reset()}>Try again</Button>
        <Button asChild variant="outline">
          <Link href="/">Back to home</Link>
        </Button>
      </div>
    </main>
  );
}