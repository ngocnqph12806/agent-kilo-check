'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

import {
  ErrorState,
  LoadingState
} from '@/components/shared';
import { isUnauthorizedError } from '@/lib/api-client';

import { BookingsView } from '@/modules/booking/components/bookings-view';
import { useCurrentUser } from '@/modules/auth/hooks/use-auth-mutations';

/**
 * Bookings list. Reads the current user via {@link useCurrentUser} so the
 * list can scope queries by `currentUserId`. Sprint 5 fix: previously only
 * checked {@code me.data} — a network failure (no backend, offline, timeout)
 * left the page on "Loading…" forever. Now surfaces isLoading vs isError
 * explicitly, and lets the api-client's 401 interceptor redirect to
 * /login for the auth case.
 */
export default function BookingsPage() {
  const me = useCurrentUser();
  const router = useRouter();

  useEffect(() => {
    if (me.error && isUnauthorizedError(me.error)) {
      router.replace('/login');
    }
  }, [me.error, router]);

  if (me.isLoading || !me.error && !me.data) {
    return (
      <div className="container mx-auto max-w-3xl py-10">
        <LoadingState label="Loading bookings…" rows={4} />
      </div>
    );
  }

  if (me.error) {
    return (
      <div className="container mx-auto max-w-3xl py-10">
        <ErrorState
          title="Could not load bookings"
          message={
            me.error instanceof Error ? me.error.message : 'Network error.'
          }
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

  return <BookingsView currentUserId={me.data.id} />;
}
