import { apiClient } from '@/lib/api-client';

import type { CreateRatingInput, Rating, RatingPage } from './schemas';

export async function submitRating(input: CreateRatingInput): Promise<Rating> {
  const { data } = await apiClient.post<Rating>('/ratings', input);
  return data;
}

export interface ListRatingsParams {
  userId: string;
  page?: number;
  size?: number;
}

export async function listUserRatings(params: ListRatingsParams): Promise<RatingPage> {
  const { userId, page = 0, size = 20 } = params;
  const { data } = await apiClient.get<RatingPage>(`/users/${userId}/ratings`, {
    params: { page, size }
  });
  return data;
}
