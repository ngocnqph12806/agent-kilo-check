import {
  Bell,
  CalendarCheck,
  CalendarClock,
  CalendarX,
  type LucideIcon,
  Mail,
  PartyPopper,
  ShieldCheck,
  Sparkles,
  Star,
  Video
} from 'lucide-react';

import type { Notification } from './schemas';

export interface NotificationMeta {
  title: string;
  Icon: LucideIcon;
  tone: string;
  href?: string;
}

export function notificationMeta(notification: Notification): NotificationMeta {
  const fallback = titleFor(notification.type);
  return {
    title: titleFromPayload(notification) ?? fallback,
    Icon: iconFor(notification.type),
    tone: toneFor(notification.type)
  };
}

function titleFromPayload(notification: Notification): string | null {
  const payload = (notification.payload ?? {}) as Record<string, unknown>;
  return typeof payload.title === 'string' ? payload.title : null;
}

export function titleFor(type: Notification['type']): string {
  switch (type) {
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
      return 'Session started';
    case 'session_completed':
      return 'Session recap';
    case 'rating_prompt':
      return 'Rate your session';
    case 'system':
    default:
      return 'Notification';
  }
}

export function iconFor(type: Notification['type']): LucideIcon {
  switch (type) {
    case 'welcome':
      return PartyPopper;
    case 'email_verified':
      return Mail;
    case 'booking_request':
      return Bell;
    case 'booking_accepted':
      return CalendarCheck;
    case 'booking_declined':
      return CalendarX;
    case 'booking_cancelled':
      return CalendarX;
    case 'booking_reminder_24h':
    case 'booking_reminder_1h':
      return CalendarClock;
    case 'session_started':
      return Video;
    case 'session_completed':
      return Sparkles;
    case 'rating_prompt':
      return Star;
    case 'system':
    default:
      return ShieldCheck;
  }
}

export function toneFor(type: Notification['type']): string {
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
