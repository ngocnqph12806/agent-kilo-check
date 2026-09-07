'use client';

import { BookingsView } from '@/modules/booking/components/bookings-view';
import { useCurrentUser } from '@/modules/auth/hooks/use-auth-mutations';

export default function BookingsPage() {
  const me = useCurrentUser();
  if (!me.data) {
    return (
      <main className="container mx-auto max-w-3xl py-20 text-center text-sm text-muted-foreground">
        Loading…
      </main>
    );
  }
  return <BookingsView currentUserId={me.data.id} />;
}
