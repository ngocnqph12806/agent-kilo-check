'use client';

import {
  Calendar,
  CheckCircle2,
  Clock,
  MapPin,
  Video,
  XCircle
} from 'lucide-react';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import {
  statusToBadgeClasses,
  useAcceptBooking,
  useCancelBooking,
  useCompleteBooking,
  useDeclineBooking,
  useStartBooking
} from '../hooks/use-bookings';
import type { BookingStatus, BookingSummary } from '../lib/schemas';

export interface BookingListItemProps {
  booking: BookingSummary;
  currentUserId: string;
  perspective: 'teacher' | 'learner';
}

export function BookingListItem({
  booking,
  currentUserId,
  perspective
}: BookingListItemProps) {
  const accept = useAcceptBooking();
  const decline = useDeclineBooking();
  const cancel = useCancelBooking();
  const start = useStartBooking();
  const complete = useCompleteBooking();

  const scheduled = new Date(booking.scheduledAt);
  const counterpartyName =
    perspective === 'teacher' ? booking.learnerName : booking.teacherName;

  const initials = counterpartyName
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? '')
    .join('');

  const monthShort = scheduled.toLocaleDateString(undefined, { month: 'short' }).toUpperCase();
  const dayNum = scheduled.toLocaleDateString(undefined, { day: 'numeric' });
  const weekday = scheduled.toLocaleDateString(undefined, { weekday: 'long' });
  const startTime = scheduled.toLocaleTimeString(undefined, {
    hour: '2-digit',
    minute: '2-digit'
  });
  const endTime = new Date(scheduled.getTime() + booking.durationMinutes * 60 * 1000)
    .toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' });

  const onAccept = () => accept.mutate(booking.id);
  const onDecline = () =>
    decline.mutate({ id: booking.id, reason: 'TEACHER_DECLINED' });
  const onCancel = () =>
    cancel.mutate({
      id: booking.id,
      input: { reason: 'OTHER', message: 'Cancelled by user' }
    });
  const onStart = () => start.mutate(booking.id);
  const onComplete = () => complete.mutate(booking.id);

  return (
    <li className="flex flex-col gap-4 rounded-2xl border border-[var(--brand-border)] bg-white p-6 shadow-brand-card sm:flex-row sm:items-center sm:justify-between">
      <div className="flex flex-1 items-start gap-4">
        <div
          aria-hidden
          className="hidden h-20 w-20 shrink-0 flex-col items-center justify-center rounded-2xl bg-gradient-to-br from-[var(--brand-hero-from)] to-[var(--brand-hero-to)] text-center sm:flex"
        >
          <span className="text-[10px] font-bold uppercase tracking-wide text-primary">
            {monthShort}
          </span>
          <span className="text-2xl font-extrabold leading-none text-[var(--brand-text-strong)]">
            {dayNum}
          </span>
          <span className="text-[10px] text-[var(--brand-text-muted)]">
            {weekday}
          </span>
        </div>
        <div
          aria-hidden
          className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-[var(--brand-hero-from)] to-[var(--brand-hero-to)] text-sm font-bold text-[var(--brand-text-strong)] sm:hidden"
        >
          {initials || '👤'}
        </div>
        <div className="flex-1 space-y-2">
          <div className="flex flex-wrap items-center gap-2">
            <span
              className={cn(
                'inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-medium capitalize',
                statusToBadgeClasses(booking.status)
              )}
            >
              {booking.status.replace('_', ' ')}
            </span>
            <h3 className="text-base font-semibold text-[var(--brand-text-strong)]">
              {booking.skillName}
            </h3>
          </div>
          <p className="text-sm text-[var(--brand-text-muted)]">
            {perspective === 'teacher' ? 'with' : 'from'}{' '}
            <span className="font-medium text-[var(--brand-text-strong)]">
              {counterpartyName}
            </span>
          </p>
          <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-[var(--brand-text-muted)]">
            <span className="inline-flex items-center gap-1">
              <Calendar className="h-3 w-3" aria-hidden />
              {scheduled.toLocaleDateString(undefined, {
                weekday: 'short',
                month: 'short',
                day: 'numeric'
              })}
            </span>
            <span className="inline-flex items-center gap-1">
              <Clock className="h-3 w-3" aria-hidden />
              {startTime} – {endTime} · {booking.durationMinutes}m
            </span>
            <span className="inline-flex items-center gap-1">
              <MapPin className="h-3 w-3" aria-hidden />
              Online (video)
            </span>
            <span className="inline-flex items-center gap-1 font-medium text-[var(--brand-text-strong)]">
              🌱 {booking.seedAmount} seeds
            </span>
          </div>
          <p className="text-base font-semibold text-[var(--brand-text-strong)] sm:hidden">
            {weekday}, {monthShort} {dayNum} · {startTime}
          </p>
        </div>
      </div>

      <div className="flex flex-wrap items-center gap-2 sm:flex-nowrap">
        <Button asChild variant="outline" className="h-10 rounded-full">
          <Link href={`/bookings/${booking.id}`}>Details</Link>
        </Button>
        <ActionButtons
          booking={booking}
          perspective={perspective}
          currentUserId={currentUserId}
          busy={
            accept.isPending ||
            decline.isPending ||
            cancel.isPending ||
            start.isPending ||
            complete.isPending
          }
          onAccept={onAccept}
          onDecline={onDecline}
          onCancel={onCancel}
          onStart={onStart}
          onComplete={onComplete}
        />
      </div>
    </li>
  );
}

interface ActionButtonsProps {
  booking: BookingSummary;
  perspective: 'teacher' | 'learner';
  currentUserId: string;
  busy: boolean;
  onAccept: () => void;
  onDecline: () => void;
  onCancel: () => void;
  onStart: () => void;
  onComplete: () => void;
}

function ActionButtons({
  booking,
  perspective,
  busy,
  onAccept,
  onDecline,
  onCancel,
  onStart,
  onComplete
}: ActionButtonsProps) {
  const buttons: React.ReactNode[] = [];
  if (booking.status === ('pending' as BookingStatus) && perspective === 'teacher') {
    buttons.push(
      <Button
        key="accept"
        className="h-10 rounded-full bg-brand-cta px-5 font-semibold text-white shadow-brand-cta hover:opacity-95"
        onClick={onAccept}
        disabled={busy}
      >
        <CheckCircle2 className="mr-1 h-4 w-4" aria-hidden />
        Accept
      </Button>,
      <Button
        key="decline"
        variant="outline"
        className="h-10 rounded-full"
        onClick={onDecline}
        disabled={busy}
      >
        <XCircle className="mr-1 h-4 w-4" aria-hidden />
        Decline
      </Button>
    );
  }
  if (
    (booking.status === ('pending' as BookingStatus) ||
      booking.status === ('confirmed' as BookingStatus)) &&
    (perspective === 'teacher' || perspective === 'learner')
  ) {
    buttons.push(
      <Button
        key="cancel"
        variant="ghost"
        className="h-10 rounded-full text-[var(--brand-rose)]"
        onClick={onCancel}
        disabled={busy}
      >
        Cancel
      </Button>
    );
  }
  if (
    booking.status === ('confirmed' as BookingStatus) &&
    withinJoinWindow(booking.scheduledAt)
  ) {
    buttons.push(
      <Button
        key="start"
        className="h-10 rounded-full bg-brand-cta px-5 font-semibold text-white shadow-brand-cta hover:opacity-95"
        onClick={onStart}
        disabled={busy}
      >
        <Video className="mr-1 h-4 w-4" aria-hidden />
        Start
      </Button>
    );
  }
  if (
    (booking.status === ('in_progress' as BookingStatus) ||
      booking.status === ('confirmed' as BookingStatus))
  ) {
    buttons.push(
      <Button
        key="complete"
        variant="outline"
        className="h-10 rounded-full"
        onClick={onComplete}
        disabled={busy}
      >
        Mark complete
      </Button>
    );
  }
  return <>{buttons}</>;
}

function withinJoinWindow(scheduledAt: string): boolean {
  const start = new Date(scheduledAt).getTime();
  const now = Date.now();
  return now >= start - 10 * 60 * 1000;
}
