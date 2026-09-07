'use client';

import { use } from 'react';

import { BookingDetailView } from '@/modules/booking/components/booking-detail-view';
import { useCurrentUser } from '@/modules/auth/hooks/use-auth-mutations';

export default function BookingDetailPage({
  params
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = use(params);
  const me = useCurrentUser();
  if (!me.data) {
    return (
      <main className="container mx-auto max-w-3xl py-20 text-center text-sm text-muted-foreground">
        Loading…
      </main>
    );
  }
  return <BookingDetailView bookingId={id} currentUserId={me.data.id} />;
}
