import * as React from 'react';

import { cn } from '@/lib/utils';

export interface SpinnerProps extends React.SVGAttributes<SVGSVGElement> {
  size?: 'sm' | 'md' | 'lg';
  label?: string;
}

const sizeMap: Record<NonNullable<SpinnerProps['size']>, string> = {
  sm: 'h-4 w-4',
  md: 'h-6 w-6',
  lg: 'h-10 w-10'
};

export function Spinner({ size = 'md', className, label = 'Loading', ...props }: SpinnerProps) {
  return (
    <svg
      role="status"
      aria-label={label}
      className={cn('animate-spin text-primary', sizeMap[size], className)}
      viewBox="0 0 24 24"
      fill="none"
      {...props}
    >
      <circle
        cx="12"
        cy="12"
        r="10"
        stroke="currentColor"
        strokeOpacity="0.2"
        strokeWidth="3"
      />
      <path
        d="M22 12a10 10 0 0 1-10 10"
        stroke="currentColor"
        strokeWidth="3"
        strokeLinecap="round"
      />
    </svg>
  );
}

export interface PageLoaderProps {
  label?: string;
}

export function PageLoader({ label = 'Loading…' }: PageLoaderProps) {
  return (
    <div
      role="status"
      aria-live="polite"
      className="flex min-h-[40vh] flex-col items-center justify-center gap-3 text-sm text-muted-foreground"
    >
      <Spinner size="lg" />
      <span>{label}</span>
    </div>
  );
}