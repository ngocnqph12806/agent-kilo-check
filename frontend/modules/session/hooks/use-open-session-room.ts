'use client';

import { useMutation } from '@tanstack/react-query';

import { openSessionRoom, type SessionRoom } from '../lib/session-api';

export function useOpenSessionRoom() {
  return useMutation<SessionRoom, Error, string>({
    mutationFn: (bookingId: string) => openSessionRoom(bookingId)
  });
}
