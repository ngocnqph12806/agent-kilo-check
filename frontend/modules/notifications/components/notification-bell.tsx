'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import { Bell, Loader2 } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import {
  useMarkAllRead,
  useMarkNotificationRead,
  useNotifications,
  useUnreadCount
} from '../hooks/use-notifications';
import type { Notification } from '../lib/schemas';

export function NotificationBell() {
  const [open, setOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement | null>(null);
  const router = useRouter();

  const unread = useUnreadCount(true);
  const inbox = useNotifications(false, 20, open);
  const markAll = useMarkAllRead();
  const markOne = useMarkNotificationRead();

  useEffect(() => {
    function onClick(e: MouseEvent) {
      if (!containerRef.current?.contains(e.target as Node)) setOpen(false);
    }
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, []);

  const unreadCount = unread.data ?? 0;

  return (
    <div ref={containerRef} className="relative">
      <button
        type="button"
        onClick={() => setOpen((state) => !state)}
        className="relative rounded-full border border-[var(--brand-border)] bg-card p-2 text-[var(--brand-text-strong)] shadow-brand-card transition-colors hover:bg-[var(--brand-hero-soft)]"
        aria-label={`Notifications (${unreadCount} unread)`}
      >
        <Bell className="h-5 w-5" aria-hidden />
        {unreadCount > 0 ? (
          <span className="absolute -right-1 -top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-rose-500 px-1 text-[10px] font-semibold text-white">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        ) : null}
      </button>

      {open ? (
        <div className="absolute right-0 z-50 mt-2 w-96 overflow-hidden rounded-2xl border border-[var(--brand-border)] bg-card text-[var(--brand-text-strong)] shadow-brand-card">
          <div className="flex items-center justify-between border-b border-[var(--brand-border)] bg-[var(--brand-hero-soft)]/40 px-4 py-3">
            <div>
              <p className="text-sm font-bold">Notifications</p>
              <p className="text-xs text-[var(--brand-text-muted)]">
                {unreadCount} unread
              </p>
            </div>
            <Button
              type="button"
              variant="ghost"
              size="sm"
              className="rounded-full text-brand-credit hover:bg-brand-credit-bg"
              disabled={unreadCount === 0 || markAll.isPending}
              onClick={() => markAll.mutate()}
            >
              Mark all read
            </Button>
          </div>

          <div className="max-h-96 overflow-auto">
            {inbox.isLoading ? (
              <div className="flex items-center justify-center gap-2 px-4 py-10 text-sm text-[var(--brand-text-muted)]">
                <Loader2 className="h-4 w-4 animate-spin" aria-hidden />
                Loading…
              </div>
            ) : inbox.error ? (
              <p className="px-4 py-10 text-center text-sm text-rose-600">
                Could not load notifications.
              </p>
            ) : inbox.data && inbox.data.items.length > 0 ? (
              <ul className="divide-y divide-[var(--brand-border)]">
                {inbox.data.items.map((n) => (
                  <NotificationRow
                    key={n.id}
                    notification={n}
                    onClick={() => {
                      markOne.mutate(n.id);
                      const target = routeFor(n);
                      if (target) router.push(target);
                      setOpen(false);
                    }}
                  />
                ))}
              </ul>
            ) : (
              <div className="px-4 py-10 text-center text-sm text-[var(--brand-text-muted)]">
                <Bell className="mx-auto mb-2 h-6 w-6 text-zinc-300" aria-hidden />
                No notifications yet.
              </div>
            )}
          </div>

          <div className="border-t border-[var(--brand-border)] bg-card px-4 py-2">
            <Link
              href="/notifications"
              onClick={() => setOpen(false)}
              className="block text-center text-xs font-semibold text-emerald-700 hover:underline"
            >
              View all notifications
            </Link>
          </div>
        </div>
      ) : null}
    </div>
  );
}

function NotificationRow({
  notification,
  onClick
}: {
  notification: Notification;
  onClick: () => void;
}) {
  const payload = (notification.payload ?? {}) as Record<string, unknown>;
  const title =
    typeof payload.title === 'string'
      ? payload.title
      : titleFor(notification);

  return (
    <li>
      <button
        type="button"
        onClick={onClick}
        className={cn(
          'flex w-full items-start gap-3 px-4 py-3 text-left transition-colors hover:bg-[var(--brand-hero-soft)]/40',
          notification.unread && 'bg-emerald-50/60'
        )}
      >
        <span
          className={cn(
            'mt-1.5 h-2 w-2 shrink-0 rounded-full',
            notification.unread ? 'bg-emerald-500' : 'bg-transparent'
          )}
          aria-hidden
        />
        <div className="flex-1 space-y-1">
          <p className="text-sm font-semibold text-[var(--brand-text-strong)]">
            {title}
          </p>
          <p className="text-xs text-[var(--brand-text-muted)]">
            {new Date(notification.createdAt).toLocaleString()}
          </p>
        </div>
      </button>
    </li>
  );
}

function titleFor(notification: Notification): string {
  switch (notification.type) {
    case 'welcome':
      return 'Welcome to SkillSeed';
    case 'email_verified':
      return 'Email verified';
    case 'booking_request':
      return 'New booking request';
    case 'booking_accepted':
      return 'Booking accepted';
    case 'booking_declined':
      return 'Booking declined';
    case 'booking_cancelled':
      return 'Booking cancelled';
    case 'booking_reminder_24h':
      return 'Session reminder: tomorrow';
    case 'booking_reminder_1h':
      return 'Session reminder: 1 hour';
    case 'session_started':
      return 'Session has started';
    case 'session_completed':
      return 'Session completed';
    case 'rating_prompt':
      return 'Rate your last session';
    case 'system':
      return 'System announcement';
    default:
      return notification.type;
  }
}

function routeFor(notification: Notification): string | null {
  const href = (notification.payload as { href?: string }).href;
  return href ?? null;
}
