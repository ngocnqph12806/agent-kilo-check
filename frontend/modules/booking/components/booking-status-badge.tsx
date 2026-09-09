import { cn } from '@/lib/utils';
import { statusToBadgeClasses } from '../hooks/use-bookings';
import type { BookingStatus } from '../lib/schemas';

/**
 * Human-readable label per BookingStatus. Replaces naive `status.replace('_', ' ')`
 * which produced awkward strings for `no_show` and `in_progress`.
 */
export const BOOKING_STATUS_LABELS: Record<BookingStatus, string> = {
  pending: 'Pending',
  confirmed: 'Confirmed',
  in_progress: 'In progress',
  completed: 'Completed',
  rated: 'Rated',
  cancelled: 'Cancelled',
  declined: 'Declined',
  expired: 'Expired',
  no_show: 'No-show'
};

export interface BookingStatusBadgeProps {
  status: BookingStatus;
  className?: string;
}

/**
 * Shared status badge for booking surfaces (T-M416). Replaces inline
 * {@code <span>} + {@code statusToBadgeClasses(status)} patterns in
 * booking-detail-view and booking-list-item so every booking surface
 * renders the same pill, label, and tone.
 */
export function BookingStatusBadge({ status, className }: BookingStatusBadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium',
        statusToBadgeClasses(status),
        className
      )}
    >
      {BOOKING_STATUS_LABELS[status]}
    </span>
  );
}
