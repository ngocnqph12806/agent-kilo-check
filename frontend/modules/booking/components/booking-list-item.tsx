'use client';

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
    <li className="flex flex-col gap-3 rounded-lg border bg-card p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
      <div className="space-y-1">
        <div className="flex items-center gap-2">
          <span
            className={cn(
              'inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium uppercase tracking-wide',
              statusToBadgeClasses(booking.status)
            )}
          >
            {booking.status.replace('_', ' ')}
          </span>
          <p className="text-sm font-medium">{booking.skillName}</p>
        </div>
        <p className="text-sm text-muted-foreground">
          {perspective === 'teacher' ? 'with' : 'from'} {counterpartyName} ·{' '}
          {scheduled.toLocaleString()} · {booking.durationMinutes}m
        </p>
        <p className="text-xs text-muted-foreground">
          {booking.seedAmount} seeds
        </p>
      </div>

      <div className="flex flex-wrap items-center gap-2">
        <Button asChild variant="outline" size="sm">
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
      <Button key="accept" size="sm" onClick={onAccept} disabled={busy}>
        Accept
      </Button>,
      <Button
        key="decline"
        size="sm"
        variant="outline"
        onClick={onDecline}
        disabled={busy}
      >
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
        size="sm"
        variant="ghost"
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
      <Button key="start" size="sm" onClick={onStart} disabled={busy}>
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
        size="sm"
        variant="secondary"
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
