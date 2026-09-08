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
      <main className="container mx-auto max-w-3xl py-20 text-center text-sm text-[var(--brand-text-muted)]">
        Loading booking…
      </main>
    );
  }

  if (isError || !data) {
    return (
      <main className="container mx-auto max-w-3xl py-20 text-center text-sm text-[var(--brand-rose)]">
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
        'bg-gradient-to-br from-[var(--brand-hero-soft)] to-[var(--brand-surface)]'
      )}
    >
      <div className="mx-auto w-full max-w-2xl rounded-2xl border border-[var(--brand-border)] bg-card p-8 shadow-brand-card sm:p-10">
        <div className="flex flex-col items-center text-center">
          <div className="relative mb-6 flex h-24 w-24 items-center justify-center">
            <span className="absolute inset-0 rounded-full bg-[var(--brand-credit-bg)]" aria-hidden />
            <CheckCircle2
              className="relative h-12 w-12 text-primary"
              aria-hidden
              strokeWidth={2.5}
            />
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
            Booking confirmed!
          </h1>
          <p className="mt-2 text-sm text-[var(--brand-text-muted)]">
            We&apos;ve notified {booking.teacher.fullName}. See you on {dateLabel}.
          </p>
        </div>

        <section className="mt-8 space-y-4 rounded-xl bg-[var(--brand-surface)] p-5">
          <header className="flex items-center gap-3">
            <div
              className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-[var(--brand-info-bg)] text-xl"
              aria-hidden
            >
              👤
            </div>
            <div className="min-w-0">
              <p className="truncate text-base font-semibold text-[var(--brand-text-strong)]">
                {booking.teacher.fullName}
              </p>
              <p className="truncate text-xs text-[var(--brand-text-muted)]">
                {booking.skill.name}
              </p>
            </div>
          </header>

          <hr className="border-[var(--brand-divider)]" />

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
          className="mt-6 flex items-start gap-3 rounded-xl bg-[var(--brand-warn)] p-4"
          role="note"
        >
          <Calendar className="mt-0.5 h-5 w-5 shrink-0 text-[var(--brand-warn-text)]" aria-hidden />
          <div className="text-xs">
            <p className="font-semibold text-[var(--brand-warn-text)]">
              Calendar invite sent to your email
            </p>
            <p className="mt-1 text-[var(--brand-warn-text)]/80">
              We&apos;ll send you a reminder 1 hour before the session starts.
            </p>
          </div>
        </aside>

        <div className="mt-8 flex flex-wrap items-center justify-center gap-3">
          <Button asChild variant="outline" className="h-11 rounded-full px-6">
            <Link href="/bookings">View bookings</Link>
          </Button>
          <Button asChild variant="outline" className="h-11 rounded-full border-primary px-6 text-primary hover:bg-[var(--brand-hero-soft)]">
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
      <dt className="text-[11px] font-medium uppercase tracking-wide text-[var(--brand-text-subtle)]">
        {label}
      </dt>
      <dd
        className={cn(
          'mt-1 text-sm font-semibold text-[var(--brand-text-strong)]',
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
    pending: 'bg-[var(--brand-pending-bg)] text-[var(--brand-pending-text)]',
    confirmed: 'bg-[var(--brand-credit-bg)] text-[var(--brand-credit-text)]',
    declined: 'bg-[var(--brand-debit-bg)] text-[var(--brand-debit-text)]',
    in_progress: 'bg-[var(--brand-info-bg)] text-[var(--brand-info-text)]',
    completed: 'bg-[var(--brand-credit-bg)] text-[var(--brand-credit-text)]',
    cancelled: 'bg-[var(--brand-debit-bg)] text-[var(--brand-debit-text)]',
    expired: 'bg-[var(--brand-debit-bg)] text-[var(--brand-debit-text)]',
    no_show: 'bg-[var(--brand-debit-bg)] text-[var(--brand-debit-text)]',
    rated: 'bg-[var(--brand-credit-bg)] text-[var(--brand-credit-text)]'
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
