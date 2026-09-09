'use client';

import { useMutation } from '@tanstack/react-query';

import { apiClient, type ApiError } from '@/lib/api-client';

export interface JoinWaitlistInput {
  email: string;
  source?: string;
  referrer?: string;
}

export interface JoinWaitlistResponse {
  message: string;
  position: number;
}

/**
 * Submit the landing-page email to the public waitlist endpoint. Always
 * returns either success or a typed error; never throws on validation.
 */
export function useJoinWaitlist() {
  return useMutation<JoinWaitlistResponse, ApiError, JoinWaitlistInput>({
    mutationFn: async (input) => {
      const { data } = await apiClient.post<JoinWaitlistResponse>(
        '/waitlist',
        input
      );
      return data;
    }
  });
}
