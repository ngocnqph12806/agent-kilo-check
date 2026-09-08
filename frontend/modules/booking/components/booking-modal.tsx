'use client';

import { Calendar, Clock, Loader2, MapPin, X } from 'lucide-react';
import { useMemo, useState } from 'react';

import { Button } from '@/components/ui/button';
import { ErrorState } from '@/components/shared';
import { cn } from '@/lib/utils';

import { useCreateBooking } from '../hooks/use-bookings';
import {
  BOOKING_DURATIONS,
  type CreateBookingInput
} from '../lib/schemas';

interface BookableSkill {
  id: string;
  name: string;
  hourlySeedRate?: number;
}

export interface BookingModalProps {
  teacherId: string;
  teacherName: string;
  currentBalance?: number;
  skills: BookableSkill[];
  slots: Array<{ startsAt: string; endsAt: string }>;
  defaultSkillId?: string;
  open: boolean;
  onClose: () => void;
  onCreated?: (bookingId: string) => void;
}

function slotLabel(slot: { startsAt: string; endsAt: string }) {
  const start = new Date(slot.startsAt);
  const end = new Date(slot.endsAt);
  return `${start.toLocaleDateString(undefined, {
    weekday: 'short',
    month: 'short',
    day: 'numeric'
  })} · ${start.toLocaleTimeString(undefined, {
    hour: '2-digit',
    minute: '2-digit'
  })} – ${end.toLocaleTimeString(undefined, {
    hour: '2-digit',
    minute: '2-digit'
  })}`;
}

export function BookingModal({
  teacherId,
  teacherName,
  currentBalance,
  skills,
  slots,
  defaultSkillId,
  open,
  onClose,
  onCreated
}: BookingModalProps) {
  const [skillId, setSkillId] = useState<string>(
    defaultSkillId ?? skills[0]?.id ?? ''
  );
  const [slotValue, setSlotValue] = useState<string>(slots[0]?.startsAt ?? '');
  const [duration, setDuration] = useState<number>(30);
  const [notes, setNotes] = useState<string>('');

  const create = useCreateBooking();

  const selectedSlot = useMemo(
    () => slots.find((s) => s.startsAt === slotValue) ?? slots[0],
    [slots, slotValue]
  );

  const seedAmount = duration;
  const remainingAfter =
    currentBalance != null ? currentBalance - seedAmount : null;

  const initials = teacherName
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? '')
    .join('');

  if (!open) {
    return null;
  }

  const submit = async () => {
    if (!skillId || !selectedSlot) return;
    const input: CreateBookingInput = {
      teacherId,
      skillId,
      scheduledAt: selectedSlot.startsAt,
      durationMinutes: duration,
      notes: notes.trim() ? notes.trim() : undefined
    };
    try {
      const booking = await create.mutateAsync(input);
      onCreated?.(booking.id);
    } catch {
      // surfaced via create.error
    }
  };

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="booking-modal-title"
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4 py-8"
    >
      <div className="w-full max-w-2xl space-y-5 rounded-2xl border border-[var(--brand-border)] bg-white p-0 shadow-brand-card">
        <header className="flex items-center justify-between rounded-t-2xl bg-[var(--brand-divider)] px-6 py-4">
          <h2
            id="booking-modal-title"
            className="text-xl font-extrabold tracking-tight text-[var(--brand-text-strong)]"
          >
            Book a session
          </h2>
          <Button
            variant="ghost"
            size="icon"
            className="h-9 w-9 rounded-full text-[var(--brand-text-muted)]"
            onClick={onClose}
            aria-label="Close"
          >
            <X className="h-5 w-5" aria-hidden />
          </Button>
        </header>

        <div className="flex items-center gap-3 px-6">
          <div
            aria-hidden
            className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-[var(--brand-hero-from)] to-[var(--brand-hero-to)] text-lg font-bold text-[var(--brand-text-strong)]"
          >
            {initials || '👤'}
          </div>
          <div className="space-y-0.5">
            <p className="text-base font-semibold text-[var(--brand-text-strong)]">
              {teacherName}
            </p>
            <p className="text-xs text-[var(--brand-text-muted)] inline-flex items-center gap-1">
              <MapPin className="h-3 w-3" aria-hidden />
              Online · video call
            </p>
          </div>
        </div>

        <div className="space-y-5 px-6 pb-6">
          <hr className="border-[var(--brand-divider)]" />

          <Field step={1} label="What do you want to learn?">
            <select
              className="flex h-11 w-full rounded-xl border border-[var(--brand-border)] bg-white px-3 text-sm text-[var(--brand-text-strong)] focus:outline-none focus:ring-2 focus:ring-primary"
              value={skillId}
              onChange={(e) => setSkillId(e.target.value)}
            >
              {skills.length === 0 ? (
                <option value="">No skills available</option>
              ) : null}
              {skills.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name}
                  {s.hourlySeedRate ? ` (${s.hourlySeedRate} seeds/hr)` : ''}
                </option>
              ))}
            </select>
          </Field>

          <Field step={2} label="Duration">
            <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
              {BOOKING_DURATIONS.map((d) => (
                <button
                  key={d}
                  type="button"
                  onClick={() => setDuration(d)}
                  className={cn(
                    'flex h-16 flex-col items-center justify-center rounded-xl border text-sm font-medium transition',
                    duration === d
                      ? 'border-primary bg-primary/5 text-[var(--brand-text-strong)]'
                      : 'border-[var(--brand-border)] bg-white text-[var(--brand-text-strong)] hover:bg-[var(--brand-divider)]'
                  )}
                >
                  <span className="text-sm font-semibold">{d} min</span>
                  <span
                    className={cn(
                      'text-xs',
                      duration === d
                        ? 'text-primary'
                        : 'text-[var(--brand-text-muted)]'
                    )}
                  >
                    {Math.round(d / 15)} seeds
                  </span>
                </button>
              ))}
            </div>
          </Field>

          <Field step={3} label="Pick a date & time">
            {slots.length === 0 ? (
              <p className="text-xs text-[var(--brand-text-muted)]">
                No free slots available in the next 14 days.
              </p>
            ) : (
              <select
                className="flex h-11 w-full rounded-xl border border-[var(--brand-border)] bg-white px-3 text-sm text-[var(--brand-text-strong)] focus:outline-none focus:ring-2 focus:ring-primary"
                value={slotValue}
                onChange={(e) => setSlotValue(e.target.value)}
              >
                {slots.slice(0, 8).map((slot) => (
                  <option key={slot.startsAt} value={slot.startsAt}>
                    {slotLabel(slot)}
                  </option>
                ))}
              </select>
            )}
            <p className="mt-2 inline-flex items-center gap-1 text-xs text-[var(--brand-text-muted)]">
              <Calendar className="h-3 w-3" aria-hidden />
              <Clock className="h-3 w-3" aria-hidden />
              Times shown in your local timezone.
            </p>
          </Field>

          <Field step={4} label="Anything specific you'd like to cover? (optional)">
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              maxLength={500}
              rows={3}
              placeholder="e.g. I'm vegetarian — would love to learn egg-free pasta options."
              className="flex w-full rounded-xl border border-[var(--brand-border)] bg-white px-3 py-2 text-sm text-[var(--brand-text-strong)] placeholder:text-[var(--brand-text-subtle)] focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </Field>

          <div
            className={cn(
              'rounded-2xl border p-4 text-sm',
              remainingAfter != null && remainingAfter < 0
                ? 'border-[var(--brand-rose)]/30 bg-[var(--brand-rose)]/5 text-[var(--brand-rose)]'
                : 'border-primary/20 bg-[var(--brand-hero-from)] text-[var(--brand-text-strong)]'
            )}
          >
            <p className="text-sm font-semibold">Booking summary</p>
            <p className="mt-1 text-xs">
              {selectedSlot
                ? `${slotLabel(selectedSlot)} · ${duration} min`
                : 'Pick a slot to see the summary'}
            </p>
            <div className="mt-2 flex items-center justify-between text-sm">
              <span>Cost</span>
              <span className="text-base font-bold">{seedAmount} seeds</span>
            </div>
            {currentBalance != null ? (
              <div className="flex items-center justify-between text-xs">
                <span>Balance after booking</span>
                <span className="font-semibold">
                  {remainingAfter != null && remainingAfter < 0 ? 0 : remainingAfter} seeds
                </span>
              </div>
            ) : null}
          </div>

          {create.error ? (
            <ErrorState
              title="Could not create booking"
              message={extractErrorMessage(create.error)}
            />
          ) : null}

          <div className="flex flex-wrap items-center justify-end gap-2 pt-2">
            <Button
              variant="outline"
              className="h-11 rounded-full"
              onClick={onClose}
              disabled={create.isPending}
            >
              Cancel
            </Button>
            <Button
              className="h-11 rounded-full bg-brand-cta px-8 font-semibold text-white shadow-brand-cta hover:opacity-95"
              onClick={submit}
              disabled={
                create.isPending ||
                !skillId ||
                !selectedSlot ||
                (remainingAfter != null && remainingAfter < 0)
              }
            >
              {create.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" aria-hidden />
                  Booking…
                </>
              ) : (
                <>Confirm booking · {seedAmount} seeds</>
              )}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

function Field({
  step,
  label,
  children
}: {
  step?: number;
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-2">
      <p className="text-sm font-semibold text-[var(--brand-text-strong)]">
        {step ? `${step}. ` : ''}
        {label}
      </p>
      {children}
    </div>
  );
}

function extractErrorMessage(err: unknown): string {
  if (typeof err === 'object' && err && 'response' in err) {
    const response = (err as { response?: { data?: { message?: string } } })
      .response;
    if (response?.data?.message) return response.data.message;
  }
  if (err instanceof Error) return err.message;
  return 'Could not create booking.';
}
