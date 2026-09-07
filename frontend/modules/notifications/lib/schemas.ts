export interface Notification {
  id: string;
  type: string;
  payload: Record<string, unknown>;
  unread: boolean;
  createdAt: string;
  readAt?: string | null;
}

export interface NotificationPage {
  items: Notification[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  unreadCount: number;
}
