export interface SessionChatMessage {
  bookingId?: string;
  senderId?: string;
  senderName?: string;
  body: string;
  sentAt?: string;
}
