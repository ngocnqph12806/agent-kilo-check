'use client';

import { Star } from 'lucide-react';
import { useState } from 'react';

import { cn } from '@/lib/utils';

export interface StarPickerProps {
  value: number;
  onChange: (value: number) => void;
  size?: 'sm' | 'md' | 'lg';
  readOnly?: boolean;
  ariaLabel?: string;
}

const SIZE_PX: Record<NonNullable<StarPickerProps['size']>, number> = {
  sm: 16,
  md: 24,
  lg: 32
};

export function StarPicker({
  value,
  onChange,
  size = 'md',
  readOnly = false,
  ariaLabel
}: StarPickerProps) {
  const [hovered, setHovered] = useState<number | null>(null);
  const px = SIZE_PX[size];
  const active = hovered ?? value;
  return (
    <div
      role={readOnly ? undefined : 'radiogroup'}
      aria-label={ariaLabel}
      className="inline-flex items-center gap-1"
    >
      {[1, 2, 3, 4, 5].map((star) => {
        const filled = star <= active;
        return (
          <button
            key={star}
            type="button"
            disabled={readOnly}
            aria-label={`Rate ${star} ${star === 1 ? 'star' : 'stars'}`}
            aria-pressed={star === value}
            onMouseEnter={() => !readOnly && setHovered(star)}
            onMouseLeave={() => !readOnly && setHovered(null)}
            onFocus={() => !readOnly && setHovered(star)}
            onBlur={() => !readOnly && setHovered(null)}
            onClick={() => !readOnly && onChange(star)}
            className={cn(
              'rounded p-0.5 transition-colors',
              !readOnly && 'hover:bg-amber-50 focus:outline-none focus:ring-2 focus:ring-amber-300'
            )}
          >
            <Star
              width={px}
              height={px}
              className={cn(
                filled ? 'fill-amber-400 text-amber-400' : 'text-muted-foreground'
              )}
            />
          </button>
        );
      })}
    </div>
  );
}
