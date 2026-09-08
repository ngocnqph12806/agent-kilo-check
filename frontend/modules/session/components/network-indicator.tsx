'use client';

import { Signal, SignalHigh, SignalLow, SignalMedium } from 'lucide-react';

import { cn } from '@/lib/utils';

export type ConnectionQuality = 'excellent' | 'good' | 'poor' | 'bad' | 'unknown';

interface NetworkIndicatorProps {
  quality: ConnectionQuality;
  className?: string;
}

const LABELS: Record<ConnectionQuality, string> = {
  excellent: 'Excellent connection',
  good: 'Good connection',
  poor: 'Poor connection',
  bad: 'Unstable connection',
  unknown: 'Measuring connection…'
};

const TONE: Record<ConnectionQuality, string> = {
  excellent: 'bg-emerald-500/20 text-emerald-200',
  good: 'bg-emerald-500/15 text-emerald-200',
  poor: 'bg-amber-500/20 text-amber-200',
  bad: 'bg-rose-500/25 text-rose-200',
  unknown: 'bg-white/5 text-zinc-300'
};

/**
 * Network quality pill rendered inside the in-session header.
 * Quality mapping (FR-M56, 4 levels):
 *   excellent | good | poor | bad
 * `unknown` is the initial state while we wait for Daily to publish
 * its first `network-quality-change` event.
 */
export function NetworkIndicator({ quality, className }: NetworkIndicatorProps) {
  const Icon =
    quality === 'excellent' ? SignalHigh
      : quality === 'good' ? SignalMedium
        : quality === 'poor' ? SignalLow
          : quality === 'bad' ? Signal
            : SignalLow;
  return (
    <div
      className={cn(
        'inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-medium',
        TONE[quality],
        className
      )}
      aria-live="polite"
      title={LABELS[quality]}
    >
      <Icon className="h-3.5 w-3.5" aria-hidden />
      <span className="hidden sm:inline">{LABELS[quality]}</span>
    </div>
  );
}
