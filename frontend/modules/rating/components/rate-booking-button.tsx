'use client';

import { useState } from 'react';

import { RatingModal } from './rating-modal';

interface RateBookingButtonProps {
  bookingId: string;
  rateeName: string;
  disabled?: boolean;
  onSubmitted?: () => void;
}

export function RateBookingButton({ bookingId, rateeName, disabled, onSubmitted }: RateBookingButtonProps) {
  const [open, setOpen] = useState(false);

  return (
    <>
      <button
        type="button"
        onClick={() => setOpen(true)}
        disabled={disabled}
        className="inline-flex items-center justify-center rounded-md bg-amber-500 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-amber-600 disabled:pointer-events-none disabled:opacity-50"
      >
        Rate session
      </button>
      <RatingModal
        open={open}
        bookingId={bookingId}
        rateeName={rateeName}
        onClose={() => setOpen(false)}
        onSubmitted={onSubmitted}
      />
    </>
  );
}
