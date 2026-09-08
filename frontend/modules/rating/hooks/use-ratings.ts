'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import {
  createRating,
  listRatingsForUser,
  type RatingInput,
  type RatingPage
} from '../lib/rating-api';

const RATINGS_KEY = ['ratings'] as const;

export function useCreateRating() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (input: RatingInput) => createRating(input),
    onSuccess: (rating) => {
      qc.invalidateQueries({ queryKey: [...RATINGS_KEY, 'user', rating.rateeId] });
    }
  });
}

export function useUserRatings(userId: string | undefined, page = 0, size = 20) {
  return useQuery<RatingPage>({
    queryKey: userId ? [...RATINGS_KEY, 'user', userId, page, size] : [...RATINGS_KEY, 'user'],
    queryFn: () => listRatingsForUser(userId as string, page, size),
    enabled: !!userId,
    staleTime: 60 * 1000
  });
}
