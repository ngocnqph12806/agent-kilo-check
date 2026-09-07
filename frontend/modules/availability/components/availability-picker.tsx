'use client';

import { useState } from 'react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/lib/utils';

import {
  DAYS_OF_WEEK,
  TIMEZONE_OPTIONS,
  type AvailabilitySlot,
  type AvailabilityState
} from '../lib/schemas';

export interface AvailabilityPickerProps {
  value: AvailabilityState;
  onChange: (state: AvailabilityState) => void;
  className?: string;
}

export function AvailabilityPicker({
  value,
  onChange,
  className
}: AvailabilityPickerProps) {
  const [expandedDay, setExpandedDay] = useState<number | null>(null);

  function update(next: Partial<AvailabilityState>) {
    onChange({ ...value, ...next });
  }

  function setSlot(dow: number, slot: AvailabilitySlot | null) {
    const others = value.slots.filter((s) => s.dayOfWeek !== dow);
    const next = slot ? [...others, slot] : others;
    update({ slots: next });
  }

  function slotFor(dow: number): AvailabilitySlot | null {
    return value.slots.find((s) => s.dayOfWeek === dow) ?? null;
  }

  return (
    <div className={cn('space-y-4', className)}>
      <div className="space-y-2">
        <Label htmlFor="timezone">Timezone</Label>
        <select
          id="timezone"
          className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
          value={value.timezone}
          onChange={(e) => update({ timezone: e.target.value })}
        >
          {TIMEZONE_OPTIONS.map((tz) => (
            <option key={tz} value={tz}>
              {tz}
            </option>
          ))}
        </select>
      </div>

      <div className="grid grid-cols-1 gap-2 sm:grid-cols-2 lg:grid-cols-3">
        {DAYS_OF_WEEK.map((day) => {
          const slot = slotFor(day.value);
          const open = expandedDay === day.value;
          return (
            <div
              key={day.value}
              className={cn(
                'rounded-lg border bg-card p-3 text-card-foreground shadow-sm',
                slot && 'border-emerald-300'
              )}
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium">{day.label}</p>
                  <p className="text-xs text-muted-foreground">
                    {slot ? `${slot.startTime}–${slot.endTime}` : 'Not available'}
                  </p>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setExpandedDay(open ? null : day.value)}
                  type="button"
                >
                  {slot ? 'Edit' : 'Add'}
                </Button>
              </div>

              {open ? (
                <SlotEditor
                  slot={slot}
                  timezone={value.timezone}
                  onSave={(s) => {
                    setSlot(day.value, { ...s, dayOfWeek: day.value });
                    setExpandedDay(null);
                  }}
                  onClear={() => {
                    setSlot(day.value, null);
                    setExpandedDay(null);
                  }}
                />
              ) : null}
            </div>
          );
        })}
      </div>

      <p className="text-xs text-muted-foreground">
        Pick a single recurring time window per day. You can adjust the exact
        schedule when booking a session.
      </p>
    </div>
  );
}

interface SlotEditorProps {
  slot: AvailabilitySlot | null;
  timezone: string;
  onSave: (slot: { startTime: string; endTime: string; timezone: string }) => void;
  onClear: () => void;
}

function SlotEditor({ slot, timezone, onSave, onClear }: SlotEditorProps) {
  const [startTime, setStartTime] = useState(slot?.startTime ?? '09:00');
  const [endTime, setEndTime] = useState(slot?.endTime ?? '17:00');
  const valid = startTime < endTime;

  return (
    <div className="mt-3 space-y-3 border-t pt-3">
      <div className="flex gap-2">
        <div className="flex-1 space-y-1">
          <Label htmlFor={`start-${slot?.id ?? 'new'}`} className="text-xs">
            Start
          </Label>
          <Input
            id={`start-${slot?.id ?? 'new'}`}
            type="time"
            value={startTime}
            onChange={(e) => setStartTime(e.target.value)}
          />
        </div>
        <div className="flex-1 space-y-1">
          <Label htmlFor={`end-${slot?.id ?? 'new'}`} className="text-xs">
            End
          </Label>
          <Input
            id={`end-${slot?.id ?? 'new'}`}
            type="time"
            value={endTime}
            onChange={(e) => setEndTime(e.target.value)}
          />
        </div>
      </div>
      {!valid ? (
        <p className="text-xs text-destructive">End must be after start.</p>
      ) : null}
      <div className="flex justify-between gap-2">
        {slot ? (
          <Button variant="ghost" size="sm" type="button" onClick={onClear}>
            Remove
          </Button>
        ) : (
          <span />
        )}
        <Button
          size="sm"
          type="button"
          disabled={!valid}
          onClick={() => onSave({ startTime, endTime, timezone })}
        >
          Save
        </Button>
      </div>
    </div>
  );
}
