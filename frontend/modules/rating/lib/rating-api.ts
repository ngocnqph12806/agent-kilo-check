import { apiClient } from '@/lib/api-client';

export interface RatingInput {
  bookingId: string;
  overallScore: number;
  reviewText?: string;
  helpfulnessScore?: number;
  respectfulnessScore?: number;
}

export interface Rating {
  id: string;
  bookingId: string;
  raterId: string;
  rateeId: string;
  raterName: string;
  overallScore: number;
  reviewText?: string | null;
  helpfulnessScore?: number | null;
  respectfulnessScore?: number | null;
  autoRated: boolean;
  createdAt: string;
}

export interface RatingPage {
  items: Rating[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export async function createRating(input: RatingInput): Promise<Rating> {
  const { data } = await apiClient.post<Rating>('/ratings', input);
  return data;
}

export async function listRatingsForUser(
  userId: string,
  page = 0,
  size = 20
): Promise<RatingPage> {
  const { data } = await apiClient.get<RatingPage>(`/users/${userId}/ratings`, {
    params: { page, size }
  });
  return data;
}
