'use client';

import { useEffect, type ReactNode } from 'react';
import { useRouter } from 'next/navigation';

import {
  ErrorState,
  LoadingState
} from '@/components/shared';
import { isUnauthorizedError } from '@/lib/api-client';

import { useCurrentUser } from '../hooks/use-auth-mutations';

export interface RequireCurrentUserProps {
  children: (userId: string) => ReactNode;
  loadingLabel?: string;
  errorTitle?: string;
  loginPath?: string;
}

/**
 * Render-prop helper for pages that need the current user's id.
 *
 * <p>Sprint 5 fix: prior to this helper, every page did
 * {@code const me = useCurrentUser(); if (!me.data) return <Loading… />;},
 * which left the page stuck on "Loading…" forever whenever
 * {@code GET /api/v1/users/me} failed (no backend, offline, timeout, 5xx).
 *
 * <p>This component handles the three real states explicitly:
 * <ul>
 *   <li>isLoading (or no data + no error) → {@link LoadingState}</li>
 *   <li>isError with HTTP 401 → redirect to /login via the router</li>
 *   <li>isError otherwise → {@link ErrorState} with retry button</li>
 * </ul>
 */
export function RequireCurrentUser({
  children,
  loadingLabel = 'Loading…',
  errorTitle = 'Could not load your account',
  loginPath = '/login'
}: RequireCurrentUserProps) {
  const me = useCurrentUser();
  const router = useRouter();

  useEffect(() => {
    if (me.error && isUnauthorizedError(me.error)) {
      router.replace(loginPath);
    }
  }, [me.error, router, loginPath]);

  if (me.isLoading || (!me.error && !me.data)) {
    return (
      <div className="container mx-auto max-w-3xl py-10">
        <LoadingState label={loadingLabel} rows={4} />
      </div>
    );
  }

  if (me.error) {
    return (
      <div className="container mx-auto max-w-3xl py-10">
        <ErrorState
          title={errorTitle}
          message={me.error instanceof Error ? me.error.message : 'Network error.'}
          onRetry={() => {
            void me.refetch();
          }}
        />
      </div>
    );
  }

  if (!me.data) {
    return null;
  }

  return <>{children(me.data.id)}</>;
}
