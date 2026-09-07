import { apiClient } from '@/lib/api-client';

import type {
  Booking,
  BookingPage,
  BookingStatus,
  CancelBookingInput,
  CreateBookingInput
} from './schemas';

export type BookingRole = 'teacher' | 'learner';

export interface ListBookingsParams {
  role: BookingRole;
  status?: BookingStatus[];
  page?: number;
  size?: number;
}

export async function createBooking(input: CreateBookingInput): Promise<Booking> {
  const idempotencyKey =
    typeof crypto !== 'undefined' && 'randomUUID' in crypto
      ? crypto.randomUUID()
      : `${Date.now()}-${Math.random()}`;
  const { data } = await apiClient.post<Booking>('/bookings', input, {
    headers: { 'Idempotency-Key': idempotencyKey }
  });
  return data;
}

export async function getBooking(id: string): Promise<Booking> {
  const { data } = await apiClient.get<Booking>(`/bookings/${id}`);
  return data;
}

export async function listMyBookings(params: ListBookingsParams): Promise<BookingPage> {
  const { role, status, page = 0, size = 20 } = params;
  const { data } = await apiClient.get<BookingPage>('/bookings/me', {
    params: {
      role,
      status: status && status.length > 0 ? status.join(',') : undefined,
      page,
      size
    }
  });
  return data;
}

export async function acceptBooking(id: string): Promise<Booking> {
  const { data } = await apiClient.post<Booking>(`/bookings/${id}/accept`);
  return data;
}

export async function declineBooking(id: string, reason?: string): Promise<Booking> {
  const { data } = await apiClient.post<Booking>(`/bookings/${id}/decline`, {
    reason
  });
  return data;
}

export async function cancelBooking(
  id: string,
  input: CancelBookingInput
): Promise<Booking> {
  const { data } = await apiClient.post<Booking>(`/bookings/${id}/cancel`, input);
  return data;
}

export async function startBooking(id: string): Promise<Booking> {
  const { data } = await apiClient.post<Booking>(`/bookings/${id}/start`);
  return data;
}

export async function completeBooking(id: string): Promise<Booking> {
  const { data } = await apiClient.post<Booking>(`/bookings/${id}/complete`);
  return data;
}
