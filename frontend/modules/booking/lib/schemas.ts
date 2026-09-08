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

/**
 * Stable enum mirroring backend {@code com.skillseed.booking.domain.CancelReason}.
 * The wire form is the lowercase dbValue; the UI uses the SCREAMING_SNAKE_CASE
 * variant for readability.
 */
export type CancelReason =
  | 'TEACHER_UNAVAILABLE'
  | 'LEARNER_UNAVAILABLE'
  | 'TECHNICAL_ISSUE'
  | 'OTHER';

export const CANCEL_REASONS: readonly CancelReason[] = [
  'TEACHER_UNAVAILABLE',
  'LEARNER_UNAVAILABLE',
  'TECHNICAL_ISSUE',
  'OTHER'
] as const;

export const CANCEL_REASON_LABELS: Record<CancelReason, string> = {
  TEACHER_UNAVAILABLE: 'Teacher unavailable',
  LEARNER_UNAVAILABLE: "I'm unavailable",
  TECHNICAL_ISSUE: 'Technical issue',
  OTHER: 'Other'
};

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
  reason: CancelReason;
  message?: string;
}
