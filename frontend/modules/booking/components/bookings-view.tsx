'use client';

import { Calendar } from 'lucide-react';
import { useMemo, useState } from 'react';

import {
  EmptyState,
  ErrorState,
  LoadingState
} from '@/components/shared';
import { cn } from '@/lib/utils';

import { useMyBookings } from '../hooks/use-bookings';
import {
  ACTIVE_BOOKING_STATUSES,
  TERMINAL_BOOKING_STATUSES,
  type BookingStatus,
  type BookingSummary
} from '../lib/schemas';
import { BookingListItem } from './booking-list-item';

export type BookingsTab = 'upcoming' | 'past' | 'cancelled';

export interface BookingsViewProps {
  currentUserId: string;
}

type Buckets = {
  upcoming: Array<{ booking: BookingSummary; perspective: 'teacher' | 'learner' }>;
  past: Array<{ booking: BookingSummary; perspective: 'teacher' | 'learner' }>;
  cancelled: Array<{ booking: BookingSummary; perspective: 'teacher' | 'learner' }>;
};

export function BookingsView({ currentUserId }: BookingsViewProps) {
  const teacherData = useMyBookings({ role: 'teacher', size: 50 });
  const learnerData = useMyBookings({ role: 'learner', size: 50 });
  const [tab, setTab] = useState<BookingsTab>('upcoming');

  const merged = useMemo(() => {
    const items: Array<{ booking: BookingSummary; perspective: 'teacher' | 'learner' }> = [];
    const seen = new Set<string>();
    teacherData.data?.content.forEach((b) => {
      seen.add(b.id);
      items.push({ booking: b, perspective: 'teacher' });
    });
    learnerData.data?.content.forEach((b) => {
      if (seen.has(b.id)) return;
      items.push({ booking: b, perspective: 'learner' });
    });
    items.sort(
      (a, b) =>
        new Date(b.booking.scheduledAt).getTime() -
        new Date(a.booking.scheduledAt).getTime()
    );
    return items;
  }, [teacherData.data, learnerData.data]);

  const buckets: Buckets = useMemo(() => {
    const now = Date.now();
    const upcoming: Buckets['upcoming'] = [];
    const past: Buckets['past'] = [];
    const cancelled: Buckets['cancelled'] = [];
    for (const item of merged) {
      const scheduled = new Date(item.booking.scheduledAt).getTime();
      const isCancelled = (
        TERMINAL_BOOKING_STATUSES as BookingStatus[]
      ).includes(item.booking.status);
      const isUpcoming = ACTIVE_BOOKING_STATUSES.includes(
        item.booking.status as BookingStatus
      );
      if (isUpcoming && scheduled >= now) {
        upcoming.push(item);
      } else if (isCancelled) {
        cancelled.push(item);
      } else {
        past.push(item);
      }
    }
    return { upcoming, past, cancelled };
  }, [merged]);

  const isLoading = teacherData.isLoading || learnerData.isLoading;
  const error = teacherData.error ?? learnerData.error;
  const refetch = () => {
    void teacherData.refetch();
    void learnerData.refetch();
  };

  const counts = {
    upcoming: buckets.upcoming.length,
    past: buckets.past.length,
    cancelled: buckets.cancelled.length
  };

  const visibleItems = buckets[tab];

  return (
    <div className="container mx-auto max-w-4xl space-y-6 py-10">
      <header className="space-y-2">
        <h1 className="text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
          My bookings
        </h1>
        <p className="text-sm text-[var(--brand-text-muted)]">
          Manage your upcoming and past sessions.
        </p>
      </header>

      <div
        role="tablist"
        aria-label="Booking status filter"
        className="inline-flex items-center gap-1 rounded-full border border-[var(--brand-border)] bg-card p-1 shadow-brand-card"
      >
        <TabButton
          active={tab === 'upcoming'}
          onClick={() => setTab('upcoming')}
          label="Upcoming"
          count={counts.upcoming}
        />
        <TabButton
          active={tab === 'past'}
          onClick={() => setTab('past')}
          label="Past"
          count={counts.past}
        />
        <TabButton
          active={tab === 'cancelled'}
          onClick={() => setTab('cancelled')}
          label="Cancelled"
          count={counts.cancelled}
        />
      </div>

      {isLoading ? (
        <LoadingState label="Loading bookings…" rows={4} />
      ) : error ? (
        <ErrorState
          title="Could not load bookings"
          message={error instanceof Error ? error.message : 'Unknown error'}
          onRetry={refetch}
        />
      ) : visibleItems.length === 0 ? (
        <EmptyState
          icon={Calendar}
          emoji="📅"
          title={
            tab === 'upcoming'
              ? 'No upcoming sessions'
              : tab === 'past'
                ? 'No past sessions yet'
                : 'Nothing cancelled'
          }
          description={
            tab === 'upcoming'
              ? 'Browse the discover page to find a teacher and book your first session.'
              : tab === 'past'
                ? 'Completed sessions will appear here once they wrap up.'
                : 'Cancelled and declined bookings will appear here.'
          }
          {...(tab === 'upcoming'
            ? {
                action: { label: 'Browse teachers', href: '/discover' }
              }
            : {})}
        />
      ) : (
        <ul className="space-y-3">
          {visibleItems.map(({ booking, perspective }) => (
            <BookingListItem
              key={booking.id}
              booking={booking}
              perspective={perspective}
              currentUserId={currentUserId}
            />
          ))}
        </ul>
      )}
    </div>
  );
}

function TabButton({
  active,
  onClick,
  label,
  count
}: {
  active: boolean;
  onClick: () => void;
  label: string;
  count: number;
}) {
  return (
    <button
      role="tab"
      aria-selected={active}
      onClick={onClick}
      className={cn(
        'inline-flex h-10 items-center gap-2 rounded-full px-5 text-sm font-medium transition',
        active
          ? 'bg-brand-cta text-white shadow-brand-cta'
          : 'text-[var(--brand-text-muted)] hover:bg-[var(--brand-divider)]'
      )}
    >
      {label}
      <span
        className={cn(
          'inline-flex items-center justify-center rounded-full px-2 py-0.5 text-xs',
          active
            ? 'bg-white/20 text-white'
            : 'bg-[var(--brand-divider)] text-[var(--brand-text-muted)]'
        )}
      >
        {count}
      </span>
    </button>
  );
}
