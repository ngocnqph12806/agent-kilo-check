import { apiClient } from '@/lib/api-client';

import type { Notification, NotificationPage } from './schemas';

export interface NotificationListParams {
  unreadOnly?: boolean;
  page?: number;
  size?: number;
}

// Raw wire shape returned by `GET /api/v1/notifications/me`. The backend
// follows the shared PageResponse contract and exposes the row array as
// `content` (and `unreadCount` alongside). Frontend components, however,
// read `items` — so we translate once here instead of fan-out fixing it
// across every bell / page / hook that consumes the response.
interface NotificationPageWire {
  content: Notification[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  unreadCount: number;
  first?: boolean;
  last?: boolean;
  hasNext?: boolean;
  hasPrevious?: boolean;
}

export async function listNotifications(
  params: NotificationListParams
): Promise<NotificationPage> {
  const { data } = await apiClient.get<NotificationPageWire>('/notifications/me', { params });
  return {
    items: data.content ?? [],
    unreadCount: data.unreadCount ?? 0,
    totalElements: data.totalElements ?? 0,
    totalPages: data.totalPages ?? 0,
    page: data.page ?? 0,
    size: data.size ?? 0
  };
}

export async function getUnreadCount(): Promise<number> {
  const { data } = await apiClient.get<{ count: number }>('/notifications/me/unread-count');
  return data.count;
}

export async function markNotificationRead(id: string): Promise<void> {
  await apiClient.post(`/notifications/me/${id}/read`);
}

export async function markAllNotificationsRead(): Promise<number> {
  const { data } = await apiClient.post<{ markedRead: number }>(
    '/notifications/me/read-all'
  );
  return data.markedRead;
}
