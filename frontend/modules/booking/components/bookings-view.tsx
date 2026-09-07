'use client';

import { useMemo } from 'react';

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

export function BookingsView({ currentUserId }: BookingsViewProps) {
  const { data, isLoading, error } = useMyBookings({ role: 'learner', size: 50 });
  const teacherData = useMyBookings({ role: 'teacher', size: 50 });
  const learnerData = useMyBookings({ role: 'learner', size: 50 });

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

  const buckets = useMemo(() => {
    const now = Date.now();
    const upcoming: typeof merged = [];
    const past: typeof merged = [];
    const cancelled: typeof merged = [];
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

  return (
    <main className="container mx-auto max-w-3xl space-y-8 py-10">
      <header className="space-y-2">
        <h1 className="text-2xl font-bold tracking-tight">My bookings</h1>
        <p className="text-sm text-muted-foreground">
          Sessions where you're the teacher or the learner. Newest first.
        </p>
      </header>

      <Section
        title="Upcoming"
        emptyMessage="No upcoming sessions. Browse the discover page to find a teacher."
        loading={isLoading && !data}
        items={buckets.upcoming}
        currentUserId={currentUserId}
      />
      <Section
        title="Past"
        emptyMessage="No completed sessions yet."
        loading={isLoading && !data}
        items={buckets.past}
        currentUserId={currentUserId}
      />
      <Section
        title="Cancelled"
        emptyMessage="Nothing cancelled yet."
        loading={isLoading && !data}
        items={buckets.cancelled}
        currentUserId={currentUserId}
      />

      {error ? (
        <p className="rounded-md border border-destructive/30 bg-destructive/5 p-3 text-sm text-destructive">
          {error instanceof Error ? error.message : 'Could not load bookings.'}
        </p>
      ) : null}
    </main>
  );
}

interface SectionProps {
  title: string;
  emptyMessage: string;
  loading: boolean;
  items: Array<{ booking: BookingSummary; perspective: 'teacher' | 'learner' }>;
  currentUserId: string;
}

function Section({ title, emptyMessage, loading, items, currentUserId }: SectionProps) {
  return (
    <section className="space-y-3">
      <h2 className="text-base font-semibold">{title}</h2>
      {loading ? (
        <p className="text-sm text-muted-foreground">Loading…</p>
      ) : items.length === 0 ? (
        <p className="text-sm text-muted-foreground">{emptyMessage}</p>
      ) : (
        <ul className="space-y-2">
          {items.map(({ booking, perspective }) => (
            <BookingListItem
              key={booking.id}
              booking={booking}
              perspective={perspective}
              currentUserId={currentUserId}
            />
          ))}
        </ul>
      )}
    </section>
  );
}
