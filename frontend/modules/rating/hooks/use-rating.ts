'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { listUserRatings, submitRating } from '../lib/rating-api';
import type { CreateRatingInput, Rating, RatingPage } from '../lib/schemas';

export function useSubmitRating() {
  const queryClient = useQueryClient();
  return useMutation<Rating, Error, CreateRatingInput>({
    mutationFn: (input) => submitRating(input),
    onSuccess: (rating) => {
      queryClient.invalidateQueries({ queryKey: ['ratings', rateeKey(rating)] });
      queryClient.invalidateQueries({ queryKey: ['bookings'] });
    }
  });
}

export function useUserRatings(userId: string, page = 0, size = 20) {
  return useQuery<RatingPage>({
    queryKey: ['ratings', userId, page, size],
    queryFn: () => listUserRatings({ userId, page, size }),
    enabled: Boolean(userId)
  });
}

function rateeKey(rating: Rating): string {
  return rating.rateeId;
}
