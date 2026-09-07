import { apiClient } from '@/lib/api-client';

import type { NotificationPage } from './schemas';

export interface NotificationListParams {
  unreadOnly?: boolean;
  page?: number;
  size?: number;
}

export async function listNotifications(
  params: NotificationListParams
): Promise<NotificationPage> {
  const { data } = await apiClient.get<NotificationPage>('/notifications/me', { params });
  return data;
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
