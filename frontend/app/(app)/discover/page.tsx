'use client';

import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { useLogoutMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { useCurrentUser } from '@/modules/auth/hooks/use-auth-mutations';

export default function DiscoverPage() {
  const meQuery = useCurrentUser(true);
  const logout = useLogoutMutation();

  return (
    <main className="container mx-auto max-w-5xl py-10">
      <header className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Discover teachers</h1>
          <p className="text-sm text-muted-foreground">
            Find someone to learn from and book your first session.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-sm text-muted-foreground">
            {meQuery.data?.fullName ?? meQuery.data?.email ?? 'You'}
          </span>
          <Button variant="outline" size="sm" onClick={() => logout.mutate()} disabled={logout.isPending}>
            Sign out
          </Button>
        </div>
      </header>

      <section className="rounded-lg border bg-card p-12 text-center text-card-foreground shadow-sm">
        <h2 className="text-xl font-semibold">Coming soon</h2>
        <p className="mt-2 text-sm text-muted-foreground">
          The match-making feed will live here (T-M82 in the sprint plan). For now, this protected route
          proves that authentication, verification and onboarding guards work end-to-end.
        </p>
        <div className="mt-6 flex justify-center gap-2">
          <Button asChild variant="outline">
            <Link href="/onboarding">Edit profile</Link>
          </Button>
          <Button asChild>
            <Link href="/">Back to home</Link>
          </Button>
        </div>
      </section>
    </main>
  );
}
