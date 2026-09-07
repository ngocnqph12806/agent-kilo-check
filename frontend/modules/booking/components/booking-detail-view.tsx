'use client';

import Link from 'next/link';
import { useEffect, useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import {
  statusToBadgeClasses,
  useBooking,
  useCancelBooking,
  useCompleteBooking,
  useStartBooking
} from '../hooks/use-bookings';
import type { BookingStatus } from '../lib/schemas';
import { SessionPanel } from '@/modules/session';

export interface BookingDetailViewProps {
  bookingId: string;
  currentUserId: string;
}

function Countdown({ scheduledAt }: { scheduledAt: string }) {
  const target = new Date(scheduledAt).getTime();
  const [now, setNow] = useState<number>(() => Date.now());
  useEffect(() => {
    const id = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(id);
  }, []);
  const diff = target - now;
  if (Number.isNaN(diff)) return null;
  const abs = Math.abs(diff);
  const days = Math.floor(abs / (24 * 3600 * 1000));
  const hours = Math.floor((abs % (24 * 3600 * 1000)) / (3600 * 1000));
  const minutes = Math.floor((abs % (3600 * 1000)) / (60 * 1000));
  const seconds = Math.floor((abs % (60 * 1000)) / 1000);
  const prefix = diff >= 0 ? 'Starts in' : 'Started';
  return (
    <p className="text-sm font-medium">
      {prefix} {days > 0 ? `${days}d ` : ''}
      {`${hours}h ${minutes}m ${seconds}s`}
    </p>
  );
}

export function BookingDetailView({
  bookingId,
  currentUserId
}: BookingDetailViewProps) {
  const { data, isLoading, error, refetch } = useBooking(bookingId);
  const cancel = useCancelBooking();
  const start = useStartBooking();
  const complete = useCompleteBooking();

  if (isLoading) {
    return <CenteredMessage>Loading booking…</CenteredMessage>;
  }
  if (error || !data) {
    return (
      <CenteredMessage variant="error">
        {error instanceof Error ? error.message : 'Booking not found.'}
      </CenteredMessage>
    );
  }

  const isTeacher = currentUserId === data.teacher.id;
  const counterparty = isTeacher ? data.learner : data.teacher;
  const scheduledDate = new Date(data.scheduledAt);

  const withinJoinWindow =
    Date.now() >= scheduledDate.getTime() - 10 * 60 * 1000;

  const canStart =
    isParticipant(data, currentUserId) &&
    data.status === ('confirmed' as BookingStatus) &&
    withinJoinWindow;
  const canComplete =
    isParticipant(data, currentUserId) &&
    (data.status === ('in_progress' as BookingStatus) ||
      data.status === ('confirmed' as BookingStatus));
  const canCancel =
    isParticipant(data, currentUserId) &&
    (data.status === ('pending' as BookingStatus) ||
      data.status === ('confirmed' as BookingStatus));
  const showSessionPanel =
    isParticipant(data, currentUserId) &&
    (data.status === ('confirmed' as BookingStatus) ||
      data.status === ('in_progress' as BookingStatus));

  return (
    <main className="container mx-auto max-w-3xl space-y-8 py-10">
      <header className="space-y-2">
        <Button asChild variant="ghost" size="sm" className="-ml-2">
          <Link href="/bookings">← All bookings</Link>
        </Button>
        <div className="flex items-center gap-3">
          <span
            className={cn(
              'inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium uppercase tracking-wide',
              statusToBadgeClasses(data.status)
            )}
          >
            {data.status.replace('_', ' ')}
          </span>
          <h1 className="text-2xl font-bold tracking-tight">
            {data.skill.name} session
          </h1>
        </div>
        <p className="text-sm text-muted-foreground">
          {isTeacher ? 'Learner' : 'Teacher'}: {counterparty.fullName}
        </p>
      </header>

      <section className="grid gap-4 rounded-lg border bg-card p-6 shadow-sm sm:grid-cols-2">
        <Detail label="Scheduled (UTC)">
          {scheduledDate.toLocaleString()}
        </Detail>
        <Detail label="Duration">
          {data.durationMinutes} minutes
        </Detail>
        <Detail label="Seed cost">
          {data.seedAmount} seeds
        </Detail>
        <Detail label="Meeting URL">
          {data.meetingUrl ? (
            <a
              className="text-primary underline"
              href={data.meetingUrl}
              target="_blank"
              rel="noreferrer"
            >
              Join room
            </a>
          ) : (
            <span className="text-muted-foreground">
              Generated when the session starts (Sprint 3)
            </span>
          )}
        </Detail>
        {data.notes ? (
          <Detail label="Notes">{data.notes}</Detail>
        ) : null}
        {data.cancellationReason ? (
          <Detail label="Cancellation reason">{data.cancellationReason}</Detail>
        ) : null}
      </section>

      {ACTIVE_STATUSES.includes(data.status as BookingStatus) ? (
        <section className="rounded-lg border bg-muted/40 p-4">
          <Countdown scheduledAt={data.scheduledAt} />
        </section>
      ) : null}

      {showSessionPanel ? (
        <SessionPanel
          bookingId={data.id}
          scheduledAt={data.scheduledAt}
          status={data.status}
          currentUserId={currentUserId}
        />
      ) : null}

      <section className="flex flex-wrap items-center gap-2">
        {canStart ? (
          <Button
            onClick={() => start.mutate(bookingId)}
            disabled={start.isPending}
          >
            {start.isPending ? 'Starting…' : 'Start session'}
          </Button>
        ) : null}
        {canComplete ? (
          <Button
            variant="secondary"
            onClick={() => complete.mutate(bookingId)}
            disabled={complete.isPending}
          >
            {complete.isPending ? 'Completing…' : 'Mark complete'}
          </Button>
        ) : null}
        {canCancel ? (
          <Button
            variant="ghost"
            onClick={() =>
              cancel.mutate({
                id: bookingId,
                input: { reason: 'OTHER', message: 'Cancelled by user' }
              })
            }
            disabled={cancel.isPending}
          >
            {cancel.isPending ? 'Cancelling…' : 'Cancel booking'}
          </Button>
        ) : null}
        <Button variant="outline" onClick={() => refetch()}>
          Refresh
        </Button>
      </section>

      <StatusTimeline current={data.status as BookingStatus} />
    </main>
  );
}

const ACTIVE_STATUSES: BookingStatus[] = [
  'pending',
  'confirmed',
  'in_progress'
];

const TIMELINE: BookingStatus[] = [
  'pending',
  'confirmed',
  'in_progress',
  'completed'
];

function StatusTimeline({ current }: { current: BookingStatus }) {
  const cancelled = current === 'cancelled' || current === 'declined'
    || current === 'expired' || current === 'no_show';
  const reachedIndex = cancelled
    ? -1
    : TIMELINE.indexOf(current);
  return (
    <section className="space-y-2">
      <h2 className="text-base font-semibold">Lifecycle</h2>
      <ol className="flex flex-wrap items-center gap-2 text-xs">
        {TIMELINE.map((step, idx) => (
          <li
            key={step}
            className={cn(
              'rounded-full border px-3 py-1 capitalize',
              idx <= reachedIndex
                ? 'border-primary bg-primary/10 text-primary'
                : 'border-muted bg-muted text-muted-foreground'
            )}
          >
            {step.replace('_', ' ')}
          </li>
        ))}
        {cancelled ? (
          <li className="rounded-full border border-destructive bg-destructive/10 px-3 py-1 text-destructive">
            {current.replace('_', ' ')}
          </li>
        ) : null}
      </ol>
    </section>
  );
}

function Detail({
  label,
  children
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-1">
      <p className="text-xs uppercase tracking-wide text-muted-foreground">
        {label}
      </p>
      <div className="text-sm">{children}</div>
    </div>
  );
}

function isParticipant(
  booking: { teacher: { id: string }; learner: { id: string } },
  userId: string
) {
  return booking.teacher.id === userId || booking.learner.id === userId;
}

function CenteredMessage({
  children,
  variant
}: {
  children: React.ReactNode;
  variant?: 'error';
}) {
  return (
    <main className="container mx-auto max-w-3xl py-20 text-center text-sm">
      <p
        className={cn(
          'rounded-md border p-4',
          variant === 'error'
            ? 'border-destructive/30 bg-destructive/5 text-destructive'
            : 'text-muted-foreground'
        )}
      >
        {children}
      </p>
    </main>
  );
}
