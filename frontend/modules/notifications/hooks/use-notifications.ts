'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import {
  getUnreadCount,
  listNotifications,
  markAllNotificationsRead,
  markNotificationRead
} from '../lib/notifications-api';

const KEY = ['notifications', 'me'] as const;
const COUNT_KEY = ['notifications', 'unread-count'] as const;

const POLL_INTERVAL_MS = 60_000;

export function useNotifications(unreadOnly = false, size = 20, enabled = true) {
  return useQuery({
    queryKey: [...KEY, unreadOnly, size],
    queryFn: () => listNotifications({ unreadOnly, size }),
    enabled,
    refetchInterval: POLL_INTERVAL_MS,
    refetchIntervalInBackground: false,
    staleTime: 30_000
  });
}

export function useUnreadCount(enabled = true) {
  return useQuery({
    queryKey: COUNT_KEY,
    queryFn: getUnreadCount,
    enabled,
    refetchInterval: POLL_INTERVAL_MS,
    refetchIntervalInBackground: false,
    staleTime: 30_000
  });
}

export function useMarkNotificationRead() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => markNotificationRead(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEY });
      qc.invalidateQueries({ queryKey: COUNT_KEY });
    }
  });
}

export function useMarkAllRead() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: markAllNotificationsRead,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEY });
      qc.invalidateQueries({ queryKey: COUNT_KEY });
    }
  });
}
