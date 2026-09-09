'use client';

import { use } from 'react';

import { BookingDetailView } from '@/modules/booking/components/booking-detail-view';
import { RequireCurrentUser } from '@/modules/auth/components/require-current-user';

export default function BookingDetailPage({
  params
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = use(params);

  return (
    <RequireCurrentUser loadingLabel="Loading booking…">
      {(currentUserId) => (
        <BookingDetailView bookingId={id} currentUserId={currentUserId} />
      )}
    </RequireCurrentUser>
  );
}
