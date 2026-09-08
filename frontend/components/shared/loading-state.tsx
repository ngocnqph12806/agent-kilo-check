import { cn } from '@/lib/utils';
import { Skeleton } from '@/components/ui/skeleton';

export interface LoadingStateProps {
  label?: string;
  rows?: number;
  className?: string;
}

export function LoadingState({ label = 'Loading…', rows = 3, className }: LoadingStateProps) {
  return (
    <div
      role="status"
      aria-live="polite"
      className={cn('space-y-4 rounded-2xl border border-[var(--brand-border)] bg-card p-6 shadow-brand-card', className)}
    >
      <div className="flex items-center gap-3 text-sm text-[var(--brand-text-muted)]">
        <span className="inline-block h-2 w-2 animate-pulse rounded-full bg-primary" aria-hidden />
        {label}
      </div>
      <div className="space-y-3">
        {Array.from({ length: rows }).map((_, idx) => (
          <Skeleton key={idx} className="h-12 w-full" />
        ))}
      </div>
    </div>
  );
}
