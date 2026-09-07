'use client';

import { useMemo, useState } from 'react';

import { Button } from '@/components/ui/button';
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

function toIsoLocal(date: Date): string {
  const pad = (n: number) => `${n}`.padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function slotToLocalInput(slot: { startsAt: string; endsAt: string }) {
  const start = new Date(slot.startsAt);
  const end = new Date(slot.endsAt);
  return {
    value: slot.startsAt,
    label: `${start.toLocaleString([], {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })} → ${end.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit'
    })}`
  };
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

  const seedAmount = useMemo(() => duration, [duration]);
  const remainingAfter = currentBalance != null ? currentBalance - seedAmount : null;

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
      // error surfaced via create.error below
    }
  };

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="booking-modal-title"
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4"
    >
      <div className="w-full max-w-lg space-y-4 rounded-lg border bg-card p-6 shadow-lg">
        <div className="flex items-start justify-between">
          <div>
            <h2 id="booking-modal-title" className="text-lg font-semibold">
              Book a session with {teacherName}
            </h2>
            <p className="text-sm text-muted-foreground">
              Pick a skill, slot, and duration. Seeds are held in escrow until
              the session completes.
            </p>
          </div>
          <Button variant="ghost" size="sm" onClick={onClose} aria-label="Close">
            ✕
          </Button>
        </div>

        <Field label="Skill">
          <select
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
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

        <Field label="Time slot (next 14 days)">
          {slots.length === 0 ? (
            <p className="text-xs text-muted-foreground">
              No free slots available in the next 14 days.
            </p>
          ) : (
            <select
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
              value={slotValue}
              onChange={(e) => setSlotValue(e.target.value)}
            >
              {slots.slice(0, 8).map((slot) => {
                const opt = slotToLocalInput(slot);
                return (
                  <option key={slot.startsAt} value={slot.startsAt}>
                    {opt.label}
                  </option>
                );
              })}
            </select>
          )}
        </Field>

        <Field label="Duration">
          <div className="flex gap-2">
            {BOOKING_DURATIONS.map((d) => (
              <button
                key={d}
                type="button"
                onClick={() => setDuration(d)}
                className={cn(
                  'h-9 flex-1 rounded-md border text-sm font-medium transition',
                  duration === d
                    ? 'border-primary bg-primary text-primary-foreground'
                    : 'border-input bg-background hover:bg-muted'
                )}
              >
                {d}m
              </button>
            ))}
          </div>
        </Field>

        <Field label="Notes for the teacher (optional)">
          <textarea
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            maxLength={500}
            rows={3}
            placeholder="Anything they should know beforehand?"
            className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
          />
        </Field>

        <div
          className={cn(
            'rounded-md border p-3 text-sm',
            remainingAfter != null && remainingAfter < 0
              ? 'border-destructive/40 bg-destructive/5 text-destructive'
              : 'border-muted bg-muted/40 text-foreground'
          )}
        >
          <p>
            Cost: <strong>{seedAmount} seeds</strong>
          </p>
          {currentBalance != null ? (
            <p>
              You'll have{' '}
              <strong>
                {remainingAfter != null && remainingAfter < 0 ? 0 : remainingAfter}
              </strong>{' '}
              seeds left after booking.
            </p>
          ) : null}
        </div>

        {create.error ? (
          <p className="rounded-md border border-destructive/30 bg-destructive/5 p-3 text-xs text-destructive">
            {extractErrorMessage(create.error)}
          </p>
        ) : null}

        <div className="flex items-center justify-end gap-2">
          <Button variant="ghost" onClick={onClose} disabled={create.isPending}>
            Cancel
          </Button>
          <Button
            onClick={submit}
            disabled={
              create.isPending ||
              !skillId ||
              !selectedSlot ||
              (remainingAfter != null && remainingAfter < 0)
            }
          >
            {create.isPending ? 'Booking…' : `Confirm ${seedAmount} seeds`}
          </Button>
        </div>
      </div>
    </div>
  );
}

function Field({
  label,
  children
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-1">
      <p className="text-xs font-medium text-muted-foreground">{label}</p>
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
