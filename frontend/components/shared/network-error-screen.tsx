'use client';

import * as React from 'react';
import Link from 'next/link';
import { WifiOff } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import { useNetworkState } from './hooks/use-network-state';

export interface NetworkErrorScreenProps {
  onRetry?: () => void;
  className?: string;
}

export function NetworkErrorScreen({ onRetry, className }: NetworkErrorScreenProps) {
  const { online } = useNetworkState();
  const [retrying, setRetrying] = React.useState(false);

  const handleRetry = React.useCallback(() => {
    if (!onRetry) return;
    setRetrying(true);
    Promise.resolve(onRetry())
      .catch(() => undefined)
      .finally(() => setRetrying(false));
  }, [onRetry]);

  return (
    <main
      role="alert"
      aria-live="assertive"
      className={cn(
        'flex min-h-[60vh] flex-col items-center justify-center bg-[var(--brand-surface)] px-4',
        className
      )}
    >
      <div className="flex max-w-md flex-col items-center text-center">
        <div className="mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-[var(--brand-warn)] shadow-brand-card">
          <WifiOff className="h-12 w-12 text-[var(--brand-warn-text)]" aria-hidden />
        </div>
        <p className="mb-2 inline-flex items-center gap-2 rounded-full border border-[var(--brand-border)] bg-card px-3 py-1 text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-muted)] shadow-brand-card">
          <span aria-hidden>📶</span> Connection
        </p>
        <h1 className="text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
          {online ? 'Can\u2019t reach SkillSeed' : 'You\u2019re offline'}
        </h1>
        <p className="mt-3 max-w-md text-base text-[var(--brand-text-muted)]">
          {online
            ? 'Our servers aren\u2019t responding right now. Check your connection or try again in a moment.'
            : 'Check your internet connection. We\u2019ll keep things ready — once you\u2019re back online, hit retry and we\u2019ll catch you up.'}
        </p>
        <div className="mt-6 flex flex-wrap justify-center gap-3">
          <Button
            type="button"
            variant="brand"
            className="rounded-full"
            disabled={retrying}
            onClick={handleRetry}
          >
            {retrying ? 'Retrying…' : '↻ Retry'}
          </Button>
          <Button asChild variant="outline" className="rounded-full">
            <Link href="/">🏠 Back to home</Link>
          </Button>
        </div>
        <p className="mt-8 text-xs text-[var(--brand-text-subtle)]">
          Bookmarked content is still available below.
        </p>
      </div>
    </main>
  );
}

export default NetworkErrorScreen;