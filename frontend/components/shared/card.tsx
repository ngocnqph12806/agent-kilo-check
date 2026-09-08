import { type HTMLAttributes, forwardRef } from 'react';

import { cn } from '@/lib/utils';

/**
 * Shared rounded-2xl card surface used by every list-item / detail
 * screen. Centralises the rounded border, brand-card shadow, and
 * default padding so modules stop copy-pasting the same wrapper
 * (audit #56).
 */
export const Card = forwardRef<HTMLDivElement, HTMLAttributes<HTMLDivElement>>(
  function Card({ className, ...rest }, ref) {
    return (
      <div
        ref={ref}
        className={cn(
          'rounded-2xl border bg-card text-card-foreground shadow-brand-card',
          className
        )}
        {...rest}
      />
    );
  }
);

export const CardSection = forwardRef<HTMLDivElement, HTMLAttributes<HTMLDivElement>>(
  function CardSection({ className, ...rest }, ref) {
    return (
      <div
        ref={ref}
        className={cn(
          'rounded-2xl border border-[var(--brand-border)] bg-card p-4 shadow-brand-card sm:p-6',
          className
        )}
        {...rest}
      />
    );
  }
);
