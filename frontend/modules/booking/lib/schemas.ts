export type BookingStatus =
  | 'pending'
  | 'confirmed'
  | 'declined'
  | 'in_progress'
  | 'completed'
  | 'cancelled'
  | 'expired'
  | 'no_show'
  | 'rated';

export const BOOKING_STATUSES: BookingStatus[] = [
  'pending',
  'confirmed',
  'declined',
  'in_progress',
  'completed',
  'cancelled',
  'expired',
  'no_show',
  'rated'
];

export const ACTIVE_BOOKING_STATUSES: BookingStatus[] = [
  'pending',
  'confirmed',
  'in_progress'
];

export const TERMINAL_BOOKING_STATUSES: BookingStatus[] = [
  'completed',
  'cancelled',
  'declined',
  'expired',
  'no_show',
  'rated'
];

export const BOOKING_DURATIONS: number[] = [15, 30, 45, 60];

export const CANCEL_REASONS: string[] = [
  'TEACHER_UNAVAILABLE',
  'LEARNER_UNAVAILABLE',
  'TECHNICAL_ISSUE',
  'OTHER'
];

export interface BookingParticipant {
  id: string;
  fullName: string;
  avatarUrl?: string | null;
}

export interface BookingSkill {
  id: string;
  name: string;
  slug: string;
  category: string;
}

export interface Booking {
  id: string;
  status: BookingStatus;
  teacher: BookingParticipant;
  learner: BookingParticipant;
  skill: BookingSkill;
  scheduledAt: string;
  durationMinutes: number;
  seedAmount: number;
  meetingUrl?: string | null;
  recordingUrl?: string | null;
  notes?: string | null;
  cancellationReason?: string | null;
  cancelledBy?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface BookingSummary {
  id: string;
  status: BookingStatus;
  teacherId: string;
  teacherName: string;
  learnerId: string;
  learnerName: string;
  skillId: string;
  skillName: string;
  scheduledAt: string;
  durationMinutes: number;
  seedAmount: number;
  createdAt: string;
}

export interface BookingPage {
  content: BookingSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface CreateBookingInput {
  teacherId: string;
  skillId: string;
  scheduledAt: string;
  durationMinutes: number;
  notes?: string;
}

export interface CancelBookingInput {
  reason: string;
  message?: string;
}
