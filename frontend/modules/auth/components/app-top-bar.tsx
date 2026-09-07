'use client';

import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { useLogoutMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { NotificationBell } from '@/modules/notifications/components/notification-bell';

export function AppTopBar() {
  const logout = useLogoutMutation();

  return (
    <header className="sticky top-0 z-40 border-b bg-background/95 backdrop-blur">
      <div className="container mx-auto flex max-w-6xl items-center justify-between px-4 py-2">
        <Link href="/discover" className="text-sm font-semibold tracking-tight">
          SkillSeed
        </Link>
        <div className="flex items-center gap-2">
          <Button asChild variant="ghost" size="sm">
            <Link href="/discover">Discover</Link>
          </Button>
          <Button asChild variant="ghost" size="sm">
            <Link href="/wallet">Wallet</Link>
          </Button>
          <NotificationBell />
          <Button
            variant="outline"
            size="sm"
            onClick={() => logout.mutate()}
            disabled={logout.isPending}
          >
            Sign out
          </Button>
        </div>
      </div>
    </header>
  );
}
