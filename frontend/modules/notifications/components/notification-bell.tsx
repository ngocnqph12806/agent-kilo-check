'use client';

import { useEffect, useRef, useState } from 'react';
import { useRouter } from 'next/navigation';

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
        className="relative rounded-full border bg-background p-2 hover:bg-accent"
        aria-label={`Notifications (${unreadCount} unread)`}
      >
        <BellIcon />
        {unreadCount > 0 ? (
          <span className="absolute -right-1 -top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive px-1 text-[10px] font-semibold text-destructive-foreground">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        ) : null}
      </button>

      {open ? (
        <div className="absolute right-0 z-50 mt-2 w-80 rounded-lg border bg-popover text-popover-foreground shadow-lg">
          <div className="flex items-center justify-between border-b px-4 py-2">
            <p className="text-sm font-medium">Notifications</p>
            <Button
              variant="ghost"
              size="sm"
              type="button"
              disabled={unreadCount === 0 || markAll.isPending}
              onClick={() => markAll.mutate()}
            >
              Mark all read
            </Button>
          </div>

          <div className="max-h-96 overflow-auto">
            {inbox.isLoading ? (
              <p className="px-4 py-8 text-center text-sm text-muted-foreground">Loading…</p>
            ) : inbox.data && inbox.data.items.length > 0 ? (
              <ul className="divide-y">
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
              <p className="px-4 py-8 text-center text-sm text-muted-foreground">
                No notifications yet.
              </p>
            )}
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
  return (
    <li>
      <button
        type="button"
        onClick={onClick}
        className={cn(
          'flex w-full items-start gap-3 px-4 py-3 text-left hover:bg-accent',
          notification.unread && 'bg-primary/5'
        )}
      >
        <span
          className={cn(
            'mt-1 h-2 w-2 shrink-0 rounded-full',
            notification.unread ? 'bg-primary' : 'bg-transparent'
          )}
          aria-hidden
        />
        <div className="flex-1">
          <p className="text-sm font-medium">{titleFor(notification)}</p>
          <p className="text-xs text-muted-foreground">
            {new Date(notification.createdAt).toLocaleString()}
          </p>
        </div>
      </button>
    </li>
  );
}

function titleFor(notification: Notification): string {
  const title = (notification.payload as { title?: string }).title;
  if (title) return title;
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

function BellIcon() {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="18"
      height="18"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
      <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
    </svg>
  );
}
