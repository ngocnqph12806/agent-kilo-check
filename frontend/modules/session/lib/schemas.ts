export interface SessionRoom {
  bookingId: string;
  roomUrl: string;
  roomName: string;
  token: string;
  role: 'owner' | 'guest';
  expiresAt: string;
  scheduledAt: string;
  durationMinutes: number;
}

export interface ChatMessage {
  bookingId: string;
  userId: string;
  text: string;
  sentAt: string;
}
