'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import {
  acceptBooking,
  cancelBooking,
  completeBooking,
  createBooking,
  declineBooking,
  getBooking,
  listMyBookings,
  startBooking,
  type ListBookingsParams
} from '../lib/booking-api';
import type {
  BookingStatus,
  CancelBookingInput,
  CreateBookingInput
} from '../lib/schemas';

const LIST_KEY = ['bookings', 'list'] as const;
const DETAIL_KEY = ['bookings', 'detail'] as const;

export function useMyBookings(
  params: ListBookingsParams,
  enabled = true
) {
  return useQuery({
    queryKey: [...LIST_KEY, params.role, params.status?.join(',') ?? '', params.page ?? 0],
    queryFn: () => listMyBookings(params),
    enabled,
    staleTime: 30 * 1000
  });
}

export function useBooking(id: string | undefined, enabled = true) {
  return useQuery({
    queryKey: [...DETAIL_KEY, id],
    queryFn: () => getBooking(id as string),
    enabled: enabled && !!id,
    refetchInterval: 60 * 1000
  });
}

export function useCreateBooking() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (input: CreateBookingInput) => createBooking(input),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: LIST_KEY });
    }
  });
}

export function useAcceptBooking() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => acceptBooking(id),
    onSuccess: (booking) => {
      qc.invalidateQueries({ queryKey: LIST_KEY });
      qc.setQueryData([...DETAIL_KEY, booking.id], booking);
    }
  });
}

export function useDeclineBooking() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, reason }: { id: string; reason?: string }) =>
      declineBooking(id, reason),
    onSuccess: (booking) => {
      qc.invalidateQueries({ queryKey: LIST_KEY });
      qc.setQueryData([...DETAIL_KEY, booking.id], booking);
    }
  });
}

export function useCancelBooking() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, input }: { id: string; input: CancelBookingInput }) =>
      cancelBooking(id, input),
    onSuccess: (booking) => {
      qc.invalidateQueries({ queryKey: LIST_KEY });
      qc.setQueryData([...DETAIL_KEY, booking.id], booking);
    }
  });
}

export function useStartBooking() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => startBooking(id),
    onSuccess: (booking) => {
      qc.invalidateQueries({ queryKey: LIST_KEY });
      qc.setQueryData([...DETAIL_KEY, booking.id], booking);
    }
  });
}

export function useCompleteBooking() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => completeBooking(id),
    onSuccess: (booking) => {
      qc.invalidateQueries({ queryKey: LIST_KEY });
      qc.setQueryData([...DETAIL_KEY, booking.id], booking);
    }
  });
}

export function statusToBadgeClasses(status: BookingStatus): string {
  switch (status) {
    case 'pending':
      return 'bg-brand-pending-bg text-brand-pending';
    case 'confirmed':
      return 'bg-brand-cta text-white';
    case 'in_progress':
      return 'bg-brand-info-bg text-brand-info';
    case 'completed':
    case 'rated':
      return 'bg-brand-credit-bg text-brand-credit';
    case 'cancelled':
    case 'declined':
    case 'no_show':
      return 'bg-brand-debit-bg text-brand-debit';
    case 'expired':
      return 'bg-muted text-muted-foreground';
    default:
      return 'bg-muted text-muted-foreground';
  }
}
