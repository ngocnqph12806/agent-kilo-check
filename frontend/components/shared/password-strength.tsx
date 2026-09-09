'use client';

import { cn } from '@/lib/utils';

interface Props {
  value: number; // 0..4
  className?: string;
}

const SEGMENTS = 4;

const LABEL = ['Too weak', 'Weak', 'Fair', 'Good', 'Strong'] as const;

const COLOR = [
  'bg-destructive',
  'bg-destructive',
  'bg-amber-500',
  'bg-emerald-500',
  'bg-emerald-600'
] as const;

/**
 * 4-segment password strength meter (T-M406). Score is supplied by the
 * caller so we don't pull in zxcvbn — keeps the bundle small and
 * avoids an extra dep.
 */
export function PasswordStrength({ value, className }: Props) {
  const safe = Math.max(0, Math.min(SEGMENTS, value));
  return (
    <div
      className={cn('space-y-1', className)}
      role="meter"
      aria-valuenow={safe}
      aria-valuemin={0}
      aria-valuemax={SEGMENTS}
      aria-label="Password strength"
    >
      <div className="flex gap-1" aria-hidden>
        {Array.from({ length: SEGMENTS }, (_, idx) => (
          <span
            key={idx}
            className={cn(
              'h-1.5 flex-1 rounded-full transition-colors',
              idx < safe ? COLOR[safe] : 'bg-brand-divider'
            )}
          />
        ))}
      </div>
      <p className="text-xs text-brand-muted">
        {LABEL[safe]}
      </p>
    </div>
  );
}

/** Returns a 0..4 strength score for a password. Cheap heuristic. */
export function scorePassword(password: string): number {
  if (!password) return 0;
  let score = 0;
  if (password.length >= 8) score += 1;
  if (password.length >= 12) score += 1;
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) score += 1;
  if (/[0-9]/.test(password)) score += 1;
  if (/[^A-Za-z0-9]/.test(password)) score += 1;
  // Clamp to 0..4 — only the first 4 signals are surfaced.
  return Math.min(4, score);
}
