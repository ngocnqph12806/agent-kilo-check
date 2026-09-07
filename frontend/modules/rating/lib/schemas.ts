export type RatingDirection = 'learner_to_teacher' | 'teacher_to_learner';

export interface Rating {
  id: string;
  bookingId: string;
  raterId: string;
  raterName: string;
  rateeId: string;
  rateeName: string;
  direction: RatingDirection;
  overallScore: number | null;
  helpfulnessScore: number | null;
  respectfulnessScore: number | null;
  reviewText: string | null;
  createdAt: string;
}

export interface RatingPage {
  content: Rating[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  averageOverallScore: number | null;
}

export interface CreateRatingInput {
  bookingId: string;
  overallScore?: number;
  helpfulnessScore?: number;
  respectfulnessScore?: number;
  reviewText?: string;
}
