'use client';

import { useMutation } from '@tanstack/react-query';

import { createSessionRoom, reportSessionIssue } from '../lib/session-api';
import type { SessionRoom } from '../lib/schemas';

export function useCreateSessionRoom() {
  return useMutation<SessionRoom, Error, string>({
    mutationFn: (bookingId: string) => createSessionRoom(bookingId)
  });
}

export function useReportSessionIssue() {
  return useMutation<
    { status: string },
    Error,
    { bookingId: string; payload: { category?: string; description?: string } }
  >({
    mutationFn: ({ bookingId, payload }) => reportSessionIssue(bookingId, payload)
  });
}
