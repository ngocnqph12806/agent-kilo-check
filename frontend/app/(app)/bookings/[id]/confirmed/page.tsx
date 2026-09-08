'use client';

import { use } from 'react';

import { BookingConfirmationView } from '@/modules/booking/components/booking-confirmation-view';

export default function BookingConfirmedPage({
  params
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = use(params);
  return <BookingConfirmationView bookingId={id} />;
}
