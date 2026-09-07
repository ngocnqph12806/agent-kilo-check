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
      return 'bg-amber-100 text-amber-800 border-amber-200';
    case 'confirmed':
      return 'bg-emerald-100 text-emerald-800 border-emerald-200';
    case 'in_progress':
      return 'bg-sky-100 text-sky-800 border-sky-200';
    case 'completed':
    case 'rated':
      return 'bg-slate-100 text-slate-700 border-slate-200';
    case 'cancelled':
    case 'declined':
      return 'bg-rose-100 text-rose-800 border-rose-200';
    case 'expired':
    case 'no_show':
      return 'bg-zinc-100 text-zinc-700 border-zinc-200';
    default:
      return 'bg-slate-100 text-slate-700 border-slate-200';
  }
}
