'use client';

import * as React from 'react';
import Link from 'next/link';

import { Button } from '@/components/ui/button';

import { useNetworkState } from './hooks/use-network-state';

export interface NetworkErrorScreenProps {
  onRetry?: () => void;
}

export function NetworkErrorScreen({ onRetry }: NetworkErrorScreenProps) {
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
      className="container flex min-h-[60vh] flex-col items-center justify-center gap-6 text-center"
    >
      <span className="rounded-full border bg-muted px-3 py-1 text-xs font-medium uppercase tracking-wide text-muted-foreground">
        Connection
      </span>
      <h1 className="text-balance text-3xl font-bold tracking-tight">
        {online ? 'We can&apos;t reach SkillSeed right now.' : 'You appear to be offline.'}
      </h1>
      <p className="max-w-md text-balance text-muted-foreground">
        Check your internet connection. We&apos;ll keep things ready — once you&apos;re back online,
        hit retry and we&apos;ll catch you up.
      </p>
      <div className="flex gap-3">
        <Button type="button" disabled={retrying} onClick={handleRetry}>
          {retrying ? 'Retrying…' : 'Retry'}
        </Button>
        <Button asChild variant="outline">
          <Link href="/">Back to home</Link>
        </Button>
      </div>
    </main>
  );
}

export default NetworkErrorScreen;