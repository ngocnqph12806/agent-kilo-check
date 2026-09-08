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

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-4">
        {DAYS_OF_WEEK.map((day) => {
          const slot = slotFor(day.value);
          const open = expandedDay === day.value;
          return (
            <div
              key={day.value}
              className={cn(
                'rounded-lg border bg-card p-4 text-card-foreground shadow-sm transition-colors',
                slot && 'border-emerald-300',
                open && 'sm:col-span-2 lg:col-span-2'
              )}
            >
              <div className="flex items-center justify-between gap-2">
                <div>
                  <p className="font-medium">{day.label}</p>
                  <p className="text-xs text-muted-foreground">
                    {slot ? `${formatTime12h(slot.startTime)} – ${formatTime12h(slot.endTime)}` : 'Not available'}
                  </p>
                </div>
                <Button
                  variant={slot ? 'outline' : 'default'}
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
    <div className="mt-4 space-y-4 border-t pt-4">
      <div className="grid grid-cols-2 gap-3">
        <TimeSelect
          label="Start"
          value={startTime}
          onChange={setStartTime}
          max={endTime}
        />
        <TimeSelect
          label="End"
          value={endTime}
          onChange={setEndTime}
          min={startTime}
        />
      </div>

      <div>
        <p className="mb-1.5 text-xs font-medium text-muted-foreground">
          Quick presets
        </p>
        <div className="flex flex-wrap gap-1.5">
          {PRESETS.map((preset) => (
            <button
              key={preset.label}
              type="button"
              onClick={() => {
                setStartTime(preset.start);
                setEndTime(preset.end);
              }}
              className="rounded-full border border-input bg-background px-3 py-1 text-xs transition-colors hover:bg-accent hover:text-accent-foreground"
            >
              {preset.label}
            </button>
          ))}
        </div>
      </div>

      <div className="flex items-center justify-between gap-2 rounded-md bg-muted/50 px-3 py-2 text-xs">
        <span className="text-muted-foreground">Window</span>
        <span className="font-medium text-foreground">
          {valid
            ? `${formatTime12h(startTime)} – ${formatTime12h(endTime)} · ${durationLabel(startTime, endTime)}`
            : 'Invalid range'}
        </span>
      </div>

      {!valid ? (
        <p className="text-xs text-destructive">End time must be after start time.</p>
      ) : null}

      <div className="flex items-center justify-between gap-2 pt-1">
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

interface TimeSelectProps {
  label: string;
  value: string;
  onChange: (next: string) => void;
  min?: string;
  max?: string;
}

const HOURS = Array.from({ length: 24 }, (h) =>
  String(h).padStart(2, '0')
);

function TimeSelect({ label, value, onChange, min, max }: TimeSelectProps) {
  const [hh, mm] = value.split(':');
  const hourOptions = HOURS.filter((h) => {
    if (max && h > max.split(':')[0]) return false;
    if (min && h < min.split(':')[0]) return false;
    return true;
  });

  function setHour(next: string) {
    onChange(`${next}:${mm}`);
  }
  function setMinute(next: string) {
    onChange(`${hh}:${next}`);
  }

  return (
    <div className="space-y-1">
      <Label className="text-xs text-muted-foreground">{label}</Label>
      <div className="flex items-center gap-1 rounded-md border border-input bg-background px-1 py-1">
        <select
          aria-label={`${label} hour`}
          value={hh}
          onChange={(e) => setHour(e.target.value)}
          className="flex-1 appearance-none bg-transparent px-2 py-1 text-sm font-medium focus:outline-none"
        >
          {hourOptions.map((h) => (
            <option key={h} value={h}>
              {h}
            </option>
          ))}
        </select>
        <span className="text-sm font-medium text-muted-foreground">:</span>
        <select
          aria-label={`${label} minute`}
          value={mm}
          onChange={(e) => setMinute(e.target.value)}
          className="flex-1 appearance-none bg-transparent px-2 py-1 text-sm font-medium focus:outline-none"
        >
          {['00', '15', '30', '45'].map((m) => (
            <option key={m} value={m}>
              {m}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}

const PRESETS: { label: string; start: string; end: string }[] = [
  { label: 'Morning', start: '08:00', end: '12:00' },
  { label: 'Afternoon', start: '12:00', end: '17:00' },
  { label: 'Evening', start: '17:00', end: '21:00' },
  { label: 'Business', start: '09:00', end: '17:00' }
];

function formatTime12h(hhmm: string): string {
  const [h, m] = hhmm.split(':').map(Number);
  if (Number.isNaN(h) || Number.isNaN(m)) return hhmm;
  const period = h >= 12 ? 'PM' : 'AM';
  const hour12 = h % 12 === 0 ? 12 : h % 12;
  return `${hour12}:${String(m).padStart(2, '0')} ${period}`;
}

function durationLabel(start: string, end: string): string {
  const [sh, sm] = start.split(':').map(Number);
  const [eh, em] = end.split(':').map(Number);
  const mins = eh * 60 + em - (sh * 60 + sm);
  if (mins <= 0) return '';
  const h = Math.floor(mins / 60);
  const m = mins % 60;
  if (h === 0) return `${m}m`;
  if (m === 0) return `${h}h`;
  return `${h}h ${m}m`;
}
