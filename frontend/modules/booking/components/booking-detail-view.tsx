'use client';

import {
  ArrowRight,
  Calendar,
  CheckCircle2,
  Clock,
  Loader2,
  MapPin,
  Video,
  XCircle
} from 'lucide-react';
import Link from 'next/link';
import { useEffect, useMemo, useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  EmptyState,
  ErrorState,
  LoadingState
} from '@/components/shared';
import { cn } from '@/lib/utils';
import { RateBookingButton } from '@/modules/rating/components/rate-booking-button';
import { JoinSessionButton } from '@/modules/session/components/join-session-button';

import {
  statusToBadgeClasses,
  useAcceptBooking,
  useBooking,
  useCancelBooking,
  useCompleteBooking,
  useDeclineBooking,
  useStartBooking
} from '../hooks/use-bookings';
import type { BookingStatus } from '../lib/schemas';

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
  const isPast = diff < 0;
  return (
    <span
      className={cn(
        'inline-flex items-center gap-2 rounded-full px-3 py-1 text-sm font-medium',
        isPast
          ? 'bg-[var(--brand-warn)] text-[var(--brand-warn-text)]'
          : 'bg-[var(--brand-warn)] text-[var(--brand-warn-text)]'
      )}
    >
      <Clock className="h-4 w-4" aria-hidden />
      {isPast ? 'Started' : 'Starts in'}{' '}
      {days > 0 ? `${days}d ` : ''}
      {`${hours}h ${minutes}m ${seconds}s`}
    </span>
  );
}

function StatusPill({ status }: { status: BookingStatus }) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-medium capitalize',
        statusToBadgeClasses(status)
      )}
    >
      {status.replace('_', ' ')}
    </span>
  );
}

export function BookingDetailView({
  bookingId,
  currentUserId
}: BookingDetailViewProps) {
  const { data, isLoading, error, refetch } = useBooking(bookingId);
  const accept = useAcceptBooking();
  const decline = useDeclineBooking();
  const cancel = useCancelBooking();
  const start = useStartBooking();
  const complete = useCompleteBooking();
  const [confirmingCancel, setConfirmingCancel] = useState(false);

  if (isLoading) {
    return (
      <div className="container mx-auto max-w-4xl py-10">
        <LoadingState label="Loading booking…" rows={4} />
      </div>
    );
  }
  if (error || !data) {
    return (
      <div className="container mx-auto max-w-4xl py-10">
        <ErrorState
          title="Could not load booking"
          message={error instanceof Error ? error.message : 'Booking not found.'}
          onRetry={() => {
            void refetch();
          }}
        />
      </div>
    );
  }

  const isTeacher = currentUserId === data.teacher.id;
  const counterparty = isTeacher ? data.learner : data.teacher;
  const scheduledDate = new Date(data.scheduledAt);
  const tz = (() => {
    try {
      return Intl.DateTimeFormat().resolvedOptions().timeZone || 'UTC';
    } catch {
      return 'UTC';
    }
  })();

  const withinJoinWindow =
    Date.now() >= scheduledDate.getTime() - 10 * 60 * 1000;

  const canAccept = isTeacher && data.status === ('pending' as BookingStatus);
  const canDecline = isTeacher && data.status === ('pending' as BookingStatus);
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
  const canJoin =
    isParticipant(data, currentUserId) &&
    (data.status === ('confirmed' as BookingStatus) ||
      data.status === ('in_progress' as BookingStatus));
  const canRate =
    isParticipant(data, currentUserId) &&
    data.status === ('completed' as BookingStatus);

  const initials = useMemo(() => {
    return counterparty.fullName
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase() ?? '')
      .join('');
  }, [counterparty.fullName]);

  return (
    <div className="container mx-auto max-w-4xl space-y-6 py-10">
      <div>
        <Button asChild variant="ghost" size="sm" className="-ml-2 text-[var(--brand-text-muted)]">
          <Link href="/bookings">← All bookings</Link>
        </Button>
      </div>

      <section className="rounded-2xl border border-[var(--brand-border)] bg-card p-6 shadow-brand-card">
        <div className="flex flex-wrap items-start gap-4">
          <div
            aria-hidden
            className="flex h-16 w-16 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-[var(--brand-hero-from)] to-[var(--brand-hero-to)] text-xl font-bold text-[var(--brand-text-strong)]"
          >
            {initials || '👤'}
          </div>
          <div className="flex-1 space-y-1">
            <p className="text-xs uppercase tracking-wide text-[var(--brand-text-muted)]">
              {isTeacher ? 'Learner' : 'Teacher'}
            </p>
            <h1 className="text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
              {counterparty.fullName}
            </h1>
            <p className="text-sm text-[var(--brand-text-muted)]">
              {data.skill.name} · {data.durationMinutes} minutes
            </p>
            <div className="pt-2">
              <StatusPill status={data.status as BookingStatus} />
            </div>
          </div>
        </div>
      </section>

      <section className="rounded-2xl border border-[var(--brand-border)] bg-card p-6 shadow-brand-card">
        <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-muted)]">
          <Calendar className="h-4 w-4" aria-hidden />
          Date &amp; time
        </div>
        <p className="mt-3 text-base font-semibold text-[var(--brand-text-strong)]">
          {scheduledDate.toLocaleDateString(undefined, {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
          })}
        </p>
        <p className="mt-1 text-xs text-[var(--brand-text-muted)]">
          {scheduledDate.toLocaleTimeString(undefined, {
            hour: '2-digit',
            minute: '2-digit'
          })}
          {' · '}
          {tz}
        </p>
        <div className="mt-4">
          <Countdown scheduledAt={data.scheduledAt} />
        </div>
      </section>

      <section className="rounded-2xl border border-[var(--brand-border)] bg-card p-6 shadow-brand-card">
        <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-muted)]">
          <Video className="h-4 w-4" aria-hidden />
          Meeting
        </div>
        <p className="mt-3 text-base font-semibold text-[var(--brand-text-strong)]">
          Online (video call)
        </p>
        <p className="text-xs text-[var(--brand-text-muted)]">
          {data.meetingUrl ? (
            <a
              className="text-primary underline-offset-2 hover:underline"
              href={data.meetingUrl}
              target="_blank"
              rel="noreferrer"
            >
              Open room
            </a>
          ) : (
            'Generated when the session starts'
          )}
        </p>
      </section>

      {data.notes ? (
        <section className="rounded-2xl border border-[var(--brand-border)] bg-card p-6 shadow-brand-card">
          <p className="text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-muted)]">
            Notes
          </p>
          <p className="mt-2 text-sm text-[var(--brand-text-strong)]">{data.notes}</p>
        </section>
      ) : null}

      {data.cancellationReason || data.cancelledBy ? (
        <section className="rounded-2xl border border-[var(--brand-rose)]/30 bg-[var(--brand-rose)]/5 p-6 shadow-brand-card">
          <p className="text-xs font-semibold uppercase tracking-wide text-[var(--brand-rose)]">
            Cancellation
          </p>
          <p className="mt-2 text-sm text-[var(--brand-rose)]">
            {data.cancellationReason ?? 'Cancelled by ' + (data.cancelledBy ?? 'a participant')}.
          </p>
        </section>
      ) : null}

      <section className="rounded-2xl border border-[var(--brand-border)] bg-card p-6 shadow-brand-card">
        <p className="text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-muted)]">
          Payment
        </p>
        <div className="mt-3 flex items-center justify-between">
          <span className="text-sm text-[var(--brand-text-muted)]">
            Seed cost
          </span>
          <span className="text-sm font-semibold text-[var(--brand-text-strong)]">
            {data.seedAmount} seeds
          </span>
        </div>
      </section>

      {confirmingCancel ? (
        <section className="rounded-2xl border border-[var(--brand-rose)]/30 bg-[var(--brand-rose)]/5 p-6 shadow-brand-card">
          <div className="flex items-start gap-3">
            <XCircle className="mt-0.5 h-5 w-5 shrink-0 text-[var(--brand-rose)]" aria-hidden />
            <div className="space-y-1">
              <p className="text-base font-semibold text-[var(--brand-text-strong)]">
                Cancel this booking?
              </p>
              <p className="text-sm text-[var(--brand-text-muted)]">
                Seeds will be refunded according to the cancellation policy.
              </p>
            </div>
          </div>
          <div className="mt-4 flex flex-wrap gap-2">
            <Button type="button"
              variant="outline"
              className="h-11 rounded-full"
              onClick={() => setConfirmingCancel(false)}
              disabled={cancel.isPending}
            >
              Keep booking
            </Button>
            <Button
              type="button"
              variant="destructive-soft"
              className="h-11 rounded-full px-8 font-semibold"
              onClick={() =>
                cancel.mutate({
                  id: bookingId,
                  input: { reason: 'OTHER', message: 'Cancelled by user' }
                })
              }
              disabled={cancel.isPending}
            >
              {cancel.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" aria-hidden />
                  Cancelling…
                </>
              ) : (
                'Cancel booking'
              )}
            </Button>
          </div>
        </section>
      ) : null}

      <section className="flex flex-wrap items-center gap-2">
        {canJoin ? (
          <JoinSessionButton
            bookingId={bookingId}
            scheduledAt={data.scheduledAt}
          />
        ) : null}
        {canStart ? (
          <Button type="button"
            variant="brand" className="h-11 rounded-full px-8 font-semibold"
            onClick={() => start.mutate(bookingId)}
            disabled={start.isPending}
          >
            {start.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" aria-hidden />
                Starting…
              </>
            ) : (
              <>
                <Video className="mr-2 h-4 w-4" aria-hidden />
                Start session
              </>
            )}
          </Button>
        ) : null}
        {canAccept ? (
          <Button type="button"
            variant="brand" className="h-11 rounded-full px-8 font-semibold"
            onClick={() => accept.mutate(bookingId)}
            disabled={accept.isPending}
          >
            <CheckCircle2 className="mr-2 h-4 w-4" aria-hidden />
            Accept
          </Button>
        ) : null}
        {canDecline ? (
          <Button type="button"
            variant="outline"
            className="h-11 rounded-full"
            onClick={() =>
              decline.mutate({ id: bookingId, reason: 'TEACHER_DECLINED' })
            }
            disabled={decline.isPending}
          >
            <XCircle className="mr-2 h-4 w-4" aria-hidden />
            Decline
          </Button>
        ) : null}
        {canCancel && !confirmingCancel ? (
          <Button type="button"
            variant="ghost"
            className="h-11 rounded-full text-[var(--brand-rose)]"
            onClick={() => setConfirmingCancel(true)}
          >
            Cancel booking
          </Button>
        ) : null}
        {canComplete ? (
          <Button type="button"
            variant="outline"
            className="h-11 rounded-full"
            onClick={() => complete.mutate(bookingId)}
            disabled={complete.isPending}
          >
            {complete.isPending ? 'Completing…' : 'Mark complete'}
          </Button>
        ) : null}
        {canRate ? (
          <RateBookingButton
            bookingId={bookingId}
            rateeName={counterparty.fullName}
            onSubmitted={() => {
              void refetch();
            }}
          />
        ) : null}
        <Button type="button"
          variant="ghost"
          size="sm"
          className="ml-auto text-[var(--brand-text-muted)]"
          onClick={() => {
            void refetch();
          }}
        >
          <ArrowRight className="mr-1 h-4 w-4" aria-hidden />
          Refresh
        </Button>
      </section>

      <section className="rounded-2xl border border-[var(--brand-border)] bg-card p-6 shadow-brand-card">
        <p className="text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-muted)]">
          Lifecycle
        </p>
        <ol className="mt-3 flex flex-wrap items-center gap-2">
          {TIMELINE.map((step, idx) => {
            const cancelled =
              data.status === ('cancelled' as BookingStatus) ||
              data.status === ('declined' as BookingStatus) ||
              data.status === ('expired' as BookingStatus) ||
              data.status === ('no_show' as BookingStatus);
            const reached = !cancelled && TIMELINE.indexOf(data.status as BookingStatus) >= idx;
            return (
              <li
                key={step}
                className={cn(
                  'inline-flex items-center gap-1 rounded-full px-3 py-1 text-xs font-medium capitalize',
                  reached
                    ? 'bg-primary/10 text-primary'
                    : 'bg-[var(--brand-divider)] text-[var(--brand-text-muted)]'
                )}
              >
                {step.replace('_', ' ')}
              </li>
            );
          })}
        </ol>
      </section>
    </div>
  );
}

const TIMELINE: BookingStatus[] = [
  'pending',
  'confirmed',
  'in_progress',
  'completed'
];

function isParticipant(
  booking: { teacher: { id: string }; learner: { id: string } },
  userId: string
) {
  return booking.teacher.id === userId || booking.learner.id === userId;
}
