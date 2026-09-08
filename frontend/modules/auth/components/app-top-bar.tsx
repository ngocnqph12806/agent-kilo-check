'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { BrandLogo } from '@/components/shared/brand-logo';
import { NotificationBell } from '@/modules/notifications/components/notification-bell';
import { TopBarSearch } from '@/modules/skills/components/top-bar-search';
import { useAuthStore } from '@/modules/auth/stores/auth-store';
import { useLogoutMutation } from '@/modules/auth/hooks/use-auth-mutations';

const navItems = [
  { key: 'discover', label: 'Discover', href: '/discover' },
  { key: 'bookings', label: 'Bookings', href: '/bookings' },
  { key: 'pods', label: 'Pods', href: '/pods' },
  { key: 'events', label: 'Events', href: '/events' },
  { key: 'wallet', label: 'Wallet', href: '/wallet' }
] as const;

function getInitials(fullName?: string | null, email?: string | null): string {
  const source = (fullName && fullName.trim()) || email || '?';
  const parts = source.split(/\s+|@/).filter(Boolean);
  if (parts.length === 0) return '?';
  if (parts.length === 1) return parts[0]!.slice(0, 2).toUpperCase();
  return `${parts[0]![0] ?? ''}${parts[1]![0] ?? ''}`.toUpperCase();
}

export function AppTopBar() {
  const pathname = usePathname();
  const user = useAuthStore((state) => state.user);
  const logout = useLogoutMutation();

  const activeKey = navItems.find((item) => pathname?.startsWith(item.href))?.key;

  return (
    <header className="sticky top-0 z-40 border-b border-[var(--brand-border)] bg-background">
      <div className="mx-auto flex h-[72px] w-full max-w-[1280px] items-center justify-between gap-4 px-4 md:px-8">
        <div className="flex items-center gap-10">
          <BrandLogo href="/discover" size="md" />
          <nav className="hidden items-center gap-7 text-sm font-medium text-[var(--brand-text-muted)] md:flex">
            {navItems.map((item) => {
              const active = activeKey === item.key;
              return (
                <Link
                  key={item.key}
                  href={item.href}
                  className={cn(
                    'transition-colors hover:text-[var(--brand-text-strong)]',
                    active && 'font-semibold text-primary'
                  )}
                >
                  {item.label}
                </Link>
              );
            })}
          </nav>
        </div>

        <div className="flex flex-1 items-center justify-end gap-2">
          <TopBarSearch />
          <NotificationBell />
          <Link
            href="/settings"
            aria-label="Your account"
            className="inline-flex h-9 w-9 items-center justify-center rounded-full bg-[var(--brand-warn)] text-sm font-semibold text-[var(--brand-warn-text)]"
          >
            {getInitials(user?.fullName, user?.email)}
          </Link>
          <Button
            variant="ghost"
            size="sm"
            className="hidden md:inline-flex"
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
