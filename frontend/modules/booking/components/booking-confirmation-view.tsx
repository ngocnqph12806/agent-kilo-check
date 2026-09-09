'use client';

import Link from 'next/link';
import { Calendar, CalendarPlus, CheckCircle2 } from 'lucide-react';
import { useMemo } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import { useBooking } from '../hooks/use-bookings';
import type { Booking } from '../lib/schemas';

export interface BookingConfirmationViewProps {
  bookingId: string;
}

/**
 * Post-confirmation screen shown at /bookings/[id]/confirmed.
 * Mirrors screens-svg/04-booking/02-booking-confirmation.svg:
 * hero gradient + centered card with check-mark, booking summary,
 * reminder hint, and three CTAs (View bookings / Add to calendar / Done).
 */
export function BookingConfirmationView({ bookingId }: BookingConfirmationViewProps) {
  const { data, isLoading, isError } = useBooking(bookingId);

  if (isLoading) {
    return (
      <main className="container mx-auto max-w-3xl py-20 text-center text-sm text-brand-muted">
        Loading booking…
      </main>
    );
  }

  if (isError || !data) {
    return (
      <main className="container mx-auto max-w-3xl py-20 text-center text-sm text-brand-rose">
        We couldn&apos;t load this booking.{' '}
        <Link href="/bookings" className="underline">
          Back to bookings
        </Link>
      </main>
    );
  }

  return <ConfirmationCard booking={data} />;
}

function ConfirmationCard({ booking }: { booking: Booking }) {
  const dateLabel = useMemo(() => formatLongDate(booking.scheduledAt), [booking.scheduledAt]);
  const timeLabel = useMemo(() => formatTimeRange(booking.scheduledAt, booking.durationMinutes), [
    booking.scheduledAt,
    booking.durationMinutes
  ]);
  const icsHref = useMemo(() => buildIcsDataUrl(booking), [booking]);

  return (
    <main
      className={cn(
        'min-h-[calc(100vh-72px)] px-4 py-10',
        'bg-gradient-to-br from-brand-hero-soft to-brand-surface'
      )}
    >
      <div className="mx-auto w-full max-w-2xl rounded-2xl border border-brand-default bg-card p-8 shadow-brand-card sm:p-10">
        <div className="flex flex-col items-center text-center">
          <div className="relative mb-6 flex h-24 w-24 items-center justify-center">
            <span className="absolute inset-0 rounded-full bg-brand-credit-bg" aria-hidden />
            <CheckCircle2
              className="relative h-12 w-12 text-primary"
              aria-hidden
              strokeWidth={2.5}
            />
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-brand-strong">
            Booking confirmed!
          </h1>
          <p className="mt-2 text-sm text-brand-muted">
            We&apos;ve notified {booking.teacher.fullName}. See you on {dateLabel}.
          </p>
        </div>

        <section className="mt-8 space-y-4 rounded-xl bg-brand-surface p-5">
          <header className="flex items-center gap-3">
            <div
              className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-brand-info-bg text-xl"
              aria-hidden
            >
              👤
            </div>
            <div className="min-w-0">
              <p className="truncate text-base font-semibold text-brand-strong">
                {booking.teacher.fullName}
              </p>
              <p className="truncate text-xs text-brand-muted">
                {booking.skill.name}
              </p>
            </div>
          </header>

          <hr className="border-brand-divider" />

          <dl className="grid grid-cols-1 gap-4 text-sm sm:grid-cols-3">
            <Field label="Date" value={dateLabel} />
            <Field label="Time" value={timeLabel} />
            <Field label="Cost" value={`${booking.seedAmount} seeds`} />
          </dl>

          <dl className="grid grid-cols-1 gap-4 text-sm sm:grid-cols-2">
            <Field label="Booking ID" value={`#${booking.id.slice(0, 8).toUpperCase()}`} mono />
            <Field
              label="Status"
              value={<StatusPill status={booking.status} />}
            />
          </dl>
        </section>

        <aside
          className="mt-6 flex items-start gap-3 rounded-xl bg-brand-warn p-4"
          role="note"
        >
          <Calendar className="mt-0.5 h-5 w-5 shrink-0 text-brand-warn-text" aria-hidden />
          <div className="text-xs">
            <p className="font-semibold text-brand-warn-text">
              Calendar invite sent to your email
            </p>
            <p className="mt-1 text-brand-warn-text/80">
              We&apos;ll send you a reminder 1 hour before the session starts.
            </p>
          </div>
        </aside>

        <div className="mt-8 flex flex-wrap items-center justify-center gap-3">
          <Button asChild variant="outline" className="h-11 rounded-full px-6">
            <Link href="/bookings">View bookings</Link>
          </Button>
          <Button asChild variant="outline" className="h-11 rounded-full border-primary px-6 text-primary hover:bg-brand-hero-soft">
            <a href={icsHref} download={`skillseed-booking-${booking.id.slice(0, 8)}.ics`}>
              <CalendarPlus className="mr-2 h-4 w-4" aria-hidden />
              Add to calendar
            </a>
          </Button>
          <Button asChild className="h-11 rounded-full px-6 font-semibold">
            <Link href={`/bookings/${booking.id}`}>Done</Link>
          </Button>
        </div>
      </div>
    </main>
  );
}

function Field({
  label,
  value,
  mono = false
}: {
  label: string;
  value: React.ReactNode;
  mono?: boolean;
}) {
  return (
    <div>
      <dt className="text-[11px] font-medium uppercase tracking-wide text-brand-subtle">
        {label}
      </dt>
      <dd
        className={cn(
          'mt-1 text-sm font-semibold text-brand-strong',
          mono && 'font-mono'
        )}
      >
        {value}
      </dd>
    </div>
  );
}

function StatusPill({ status }: { status: Booking['status'] }) {
  // Map status → brand colour. Use the existing status-token conventions.
  const styles: Record<Booking['status'], string> = {
    pending: 'bg-brand-pending-bg text-brand-pending',
    confirmed: 'bg-brand-credit-bg text-brand-credit',
    declined: 'bg-brand-debit-bg text-brand-debit',
    in_progress: 'bg-brand-info-bg text-brand-info',
    completed: 'bg-brand-credit-bg text-brand-credit',
    cancelled: 'bg-brand-debit-bg text-brand-debit',
    expired: 'bg-brand-debit-bg text-brand-debit',
    no_show: 'bg-brand-debit-bg text-brand-debit',
    rated: 'bg-brand-credit-bg text-brand-credit'
  };
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold capitalize',
        styles[status]
      )}
    >
      {status.replace('_', ' ')}
    </span>
  );
}

function formatLongDate(iso: string): string {
  try {
    const d = new Date(iso);
    return d.toLocaleDateString(undefined, {
      weekday: 'long',
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  } catch {
    return iso;
  }
}

function formatTimeRange(iso: string, durationMinutes: number): string {
  try {
    const start = new Date(iso);
    const end = new Date(start.getTime() + durationMinutes * 60_000);
    const fmt = (d: Date) =>
      d.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit', hour12: false });
    return `${fmt(start)} – ${fmt(end)}`;
  } catch {
    return `${durationMinutes} min`;
  }
}

/**
 * Build a minimal RFC 5545 ICS data URL so the user can drop the
 * session into Google / Apple / Outlook calendars without us shipping
 * a calendar library. Times are emitted in UTC (Z suffix).
 */
function buildIcsDataUrl(booking: Booking): string {
  const start = new Date(booking.scheduledAt);
  const end = new Date(start.getTime() + booking.durationMinutes * 60_000);
  const stamp = new Date();
  const fmt = (d: Date) => d.toISOString().replace(/[-:]|\.\d{3}/g, '');
  const ics = [
    'BEGIN:VCALENDAR',
    'VERSION:2.0',
    'PRODID:-//SkillSeed//Booking//EN',
    'BEGIN:VEVENT',
    `UID:${booking.id}@skillseed.app`,
    `DTSTAMP:${fmt(stamp)}`,
    `DTSTART:${fmt(start)}`,
    `DTEND:${fmt(end)}`,
    `SUMMARY:${escapeIcs(`${booking.skill.name} with ${booking.teacher.fullName}`)}`,
    `DESCRIPTION:${escapeIcs('SkillSeed learning session. Join via the app at session start.')}`,
    'END:VEVENT',
    'END:VCALENDAR'
  ].join('\r\n');
  return `data:text/calendar;charset=utf-8,${encodeURIComponent(ics)}`;
}

function escapeIcs(value: string): string {
  return value.replace(/[\\,;]/g, (m) => `\\${m}`).replace(/\n/g, '\\n');
}
