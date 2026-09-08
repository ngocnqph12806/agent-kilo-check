'use client';

import * as React from 'react';

import { NetworkErrorScreen } from './network-error-screen';
import { Skeleton } from '@/components/ui/skeleton';

export interface AsyncBoundaryProps {
  isLoading: boolean;
  isError?: boolean;
  error?: Error | null;
  onRetry?: () => void;
  loadingFallback?: React.ReactNode;
  errorFallback?: React.ReactNode;
  children: React.ReactNode;
}

export function AsyncBoundary({
  isLoading,
  isError = false,
  error,
  onRetry,
  loadingFallback,
  errorFallback,
  children
}: AsyncBoundaryProps) {
  if (isLoading) {
    return (
      <>{loadingFallback ?? <DefaultLoadingFallback />}</>
    );
  }
  if (isError) {
    return (
      <>
        {errorFallback ?? (
          <NetworkErrorScreen
            onRetry={onRetry ?? (() => typeof window !== 'undefined' && window.location.reload())}
          />
        )}
        {error && (
          <p className="sr-only">Error: {error.message}</p>
        )}
      </>
    );
  }
  return <>{children}</>;
}

function DefaultLoadingFallback() {
  return (
    <div className="container space-y-4 py-10" role="status" aria-label="Loading content">
      <Skeleton className="h-8 w-1/3" />
      <Skeleton className="h-4 w-1/2" />
      <div className="space-y-3">
        {Array.from({ length: 3 }).map((_, idx) => (
          <Skeleton key={idx} className="h-20 w-full" />
        ))}
      </div>
    </div>
  );
}