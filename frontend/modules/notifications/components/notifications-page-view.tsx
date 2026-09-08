'use client';

import Link from 'next/link';
import { useMemo } from 'react';

import {
  AlertCircle,
  Bell,
  CheckCircle2,
  Loader2,
  Sparkles
} from 'lucide-react';

import { EmptyState } from '@/components/shared/empty-state';
import { ErrorState } from '@/components/shared/error-state';
import { LoadingState } from '@/components/shared/loading-state';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import {
  useMarkAllRead,
  useNotifications
} from '../hooks/use-notifications';
import type { Notification } from '../lib/schemas';

type GroupKey = 'today' | 'yesterday' | 'thisWeek' | 'earlier';

const GROUP_ORDER: GroupKey[] = ['today', 'yesterday', 'thisWeek', 'earlier'];

const GROUP_LABEL: Record<GroupKey, string> = {
  today: 'Today',
  yesterday: 'Yesterday',
  thisWeek: 'This week',
  earlier: 'Earlier'
};

const NUMBER_FORMAT = new Intl.NumberFormat('en-US');

export function NotificationsPageView() {
  const inbox = useNotifications(false, 100);
  const markAll = useMarkAllRead();

  const { data, isLoading, isError, error, refetch, isFetching } = inbox;

  const items = data?.items ?? [];
  const unreadCount = data?.unreadCount ?? items.filter((n) => n.unread).length;
  const totalCount = data?.totalElements ?? items.length;

  const grouped = useMemo(() => groupByDate(items), [items]);

  return (
    <main className="container mx-auto max-w-4xl space-y-8 py-10">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div className="space-y-1">
          <h1 className="flex items-center gap-2 text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
            <Bell className="h-7 w-7" aria-hidden />
            Notifications
          </h1>
          <p className="text-sm text-[var(--brand-text-muted)]">
            {NUMBER_FORMAT.format(unreadCount)} unread ·{' '}
            {NUMBER_FORMAT.format(totalCount)} total
          </p>
        </div>
        <div className="flex items-center gap-2">
          {isFetching && !isLoading ? (
            <Loader2
              className="h-4 w-4 animate-spin text-[var(--brand-text-muted)]"
              aria-hidden
            />
          ) : null}
          <Button
            type="button"
            variant="outline"
            className="rounded-full"
            disabled={unreadCount === 0 || markAll.isPending}
            onClick={() => markAll.mutate()}
          >
            Mark all read
          </Button>
          <Button asChild type="button" variant="outline" className="rounded-full">
            <Link href="/settings">Settings</Link>
          </Button>
        </div>
      </header>

      <section className="rounded-2xl border border-[var(--brand-border)] bg-white p-6 shadow-brand-card">
        {isLoading ? (
          <LoadingState label="Loading notifications…" rows={4} className="border-none p-0 shadow-none" />
        ) : isError ? (
          <ErrorState
            title="Could not load notifications"
            message={error instanceof Error ? error.message : undefined}
            onRetry={() => refetch()}
          />
        ) : items.length === 0 ? (
          <EmptyState
            emoji="🔔"
            icon={Bell}
            title="You're all caught up"
            description="New booking requests, reminders, and rewards will show up here."
          />
        ) : (
          <div className="space-y-6">
            {GROUP_ORDER.map((key) => {
                const list = grouped[key];
                if (!list || list.length === 0) return null;
                return (
                  <div key={key} className="space-y-3">
                    <p className="text-xs font-bold uppercase tracking-widest text-[var(--brand-text-muted)]">
                      {GROUP_LABEL[key]}
                    </p>
                    <ul className="space-y-3">
                      {list.map((notification) => (
                        <NotificationCard key={notification.id} notification={notification} />
                      ))}
                    </ul>
                  </div>
                );
              }
            )}
          </div>
        )}
      </section>
    </main>
  );
}

function NotificationCard({ notification }: { notification: Notification }) {
  const meta = describeNotification(notification);
  const Icon = meta.Icon;

  return (
    <li>
      <Link
        href={meta.href ?? '#'}
          className={cn(
          'flex items-start gap-4 rounded-2xl border bg-white p-4 shadow-brand-card transition-colors hover:bg-[var(--brand-hero-soft)]/40',
          notification.unread
            ? 'border-brand-credit ring-1 ring-brand-credit/40'
            : 'border-[var(--brand-border)]'
        )}
      >
        <span
          className={cn(
            'flex h-12 w-12 shrink-0 items-center justify-center rounded-full',
            meta.tone
          )}
        >
          <Icon className="h-5 w-5" aria-hidden />
        </span>
        <div className="min-w-0 flex-1 space-y-1">
          <p className="flex items-center gap-2 text-sm font-bold text-[var(--brand-text-strong)]">
            {meta.title}
            {notification.unread ? (
              <span
                className="inline-block h-2 w-2 shrink-0 rounded-full bg-brand-credit"
                aria-label="Unread"
              />
            ) : null}
          </p>
          {meta.body ? (
            <p className="text-sm text-[var(--brand-text-muted)]">{meta.body}</p>
          ) : null}
          <p className="text-xs text-[var(--brand-text-muted)]">
            {formatRelative(notification.createdAt)}
          </p>
        </div>
      </Link>
    </li>
  );
}

interface NotificationMeta {
  title: string;
  body?: string;
  href?: string;
  Icon: typeof Bell;
  tone: string;
}

function describeNotification(notification: Notification): NotificationMeta {
  const payload = (notification.payload ?? {}) as Record<string, unknown>;
  const title = typeof payload.title === 'string' ? payload.title : null;
  const body = typeof payload.body === 'string' ? payload.body : null;
  const href = typeof payload.href === 'string' ? payload.href : undefined;

  const fallback = titleFor(notification);
  const Icon = iconFor(notification.type);
  const tone = toneFor(notification.type);

  return { title: title ?? fallback, body: body ?? undefined, href, Icon, tone };
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

function iconFor(type: string) {
  switch (type) {
    case 'booking_accepted':
    case 'session_completed':
    case 'email_verified':
      return CheckCircle2;
    case 'booking_request':
    case 'booking_reminder_24h':
    case 'booking_reminder_1h':
      return AlertCircle;
    case 'session_started':
    case 'rating_prompt':
      return Sparkles;
    case 'system':
    default:
      return Bell;
  }
}

function toneFor(type: string): string {
  switch (type) {
    case 'booking_accepted':
    case 'session_completed':
    case 'email_verified':
      return 'bg-brand-credit-bg text-brand-credit';
    case 'booking_request':
    case 'booking_reminder_24h':
    case 'booking_reminder_1h':
      return 'bg-brand-pending-bg text-brand-pending';
    case 'session_started':
    case 'rating_prompt':
      return 'bg-brand-info-bg text-brand-info';
    case 'system':
    default:
      return 'bg-muted text-muted-foreground';
  }
}

function groupByDate(items: Notification[]): Record<GroupKey, Notification[]> {
  const now = new Date();
  const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
  const startOfYesterday = startOfToday - 24 * 60 * 60 * 1000;
  const startOfWeek = startOfToday - 7 * 24 * 60 * 60 * 1000;

  const result: Record<GroupKey, Notification[]> = {
    today: [],
    yesterday: [],
    thisWeek: [],
    earlier: []
  };

  for (const item of items) {
    const ts = new Date(item.createdAt).getTime();
    if (ts >= startOfToday) result.today.push(item);
    else if (ts >= startOfYesterday) result.yesterday.push(item);
    else if (ts >= startOfWeek) result.thisWeek.push(item);
    else result.earlier.push(item);
  }

  return result;
}

function formatRelative(iso: string): string {
  const ts = new Date(iso).getTime();
  if (Number.isNaN(ts)) return '';

  const diff = Date.now() - ts;
  const minutes = Math.round(diff / 60_000);
  if (minutes < 1) return 'Just now';
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.round(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.round(hours / 24);
  if (days < 7) return `${days}d ago`;
  return new Date(iso).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });
}
