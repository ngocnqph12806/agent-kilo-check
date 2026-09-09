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
  useAcceptBooking,
  useCancelBooking,
  useCompleteBooking,
  useDeclineBooking,
  useStartBooking
} from '../hooks/use-bookings';
import type { BookingStatus, BookingSummary } from '../lib/schemas';
import { BookingStatusBadge } from './booking-status-badge';

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
    <li className="flex flex-col gap-4 rounded-2xl border border-brand-default bg-card p-6 shadow-brand-card sm:flex-row sm:items-center sm:justify-between">
      <div className="flex flex-1 items-start gap-4">
        <div
          aria-hidden
          className="hidden h-20 w-20 shrink-0 flex-col items-center justify-center rounded-2xl bg-brand-hero-soft text-center sm:flex"
        >
          <span className="text-[10px] font-bold uppercase tracking-wide text-primary">
            {monthShort}
          </span>
          <span className="text-2xl font-extrabold leading-none text-brand-strong">
            {dayNum}
          </span>
          <span className="text-[10px] text-brand-muted">
            {weekday}
          </span>
        </div>
        <div
          aria-hidden
          className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-brand-hero-soft text-sm font-bold text-brand-strong sm:hidden"
        >
          {initials || '👤'}
        </div>
        <div className="flex-1 space-y-2">
          <div className="flex flex-wrap items-center gap-2">
            <BookingStatusBadge status={booking.status} />
            <h3 className="text-base font-semibold text-brand-strong">
              {booking.skillName}
            </h3>
          </div>
          <p className="text-sm text-brand-muted">
            {perspective === 'teacher' ? 'with' : 'from'}{' '}
            <span className="font-medium text-brand-strong">
              {counterpartyName}
            </span>
          </p>
          <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-brand-muted">
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
            <span className="inline-flex items-center gap-1 font-medium text-brand-strong">
              🌱 {booking.seedAmount} seeds
            </span>
          </div>
          <p className="text-base font-semibold text-brand-strong sm:hidden">
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
  if (booking.status === 'pending' && perspective === 'teacher') {
    buttons.push(
      <Button type="button"
        key="accept"
        variant="brand" className="h-10 rounded-full px-5 font-semibold"
        onClick={onAccept}
        disabled={busy}
      >
        <CheckCircle2 className="mr-1 h-4 w-4" aria-hidden />
        Accept
      </Button>,
      <Button type="button"
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
    (booking.status === 'pending' ||
      booking.status === 'confirmed') &&
    (perspective === 'teacher' || perspective === 'learner')
  ) {
    buttons.push(
      <Button type="button"
        key="cancel"
        variant="ghost"
        className="h-10 rounded-full text-brand-rose"
        onClick={onCancel}
        disabled={busy}
      >
        Cancel
      </Button>
    );
  }
  if (
    booking.status === 'confirmed' &&
    withinJoinWindow(booking.scheduledAt)
  ) {
    buttons.push(
      <Button type="button"
        key="start"
        variant="brand" className="h-10 rounded-full px-5 font-semibold"
        onClick={onStart}
        disabled={busy}
      >
        <Video className="mr-1 h-4 w-4" aria-hidden />
        Start
      </Button>
    );
  }
  if (
    (booking.status === 'in_progress' ||
      booking.status === 'confirmed')
  ) {
    buttons.push(
      <Button type="button"
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
