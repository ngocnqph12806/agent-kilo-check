import { AlertTriangle, RefreshCw } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

export interface ErrorStateProps {
  title?: string;
  message?: string;
  onRetry?: () => void;
  className?: string;
}

export function ErrorState({
  title = 'Something went wrong',
  message,
  onRetry,
  className
}: ErrorStateProps) {
  return (
    <div
      role="alert"
      className={cn(
        'rounded-2xl border border-brand-rose/30 bg-brand-rose/5 p-6 text-sm text-brand-rose shadow-brand-card',
        className
      )}
    >
      <div className="flex items-start gap-3">
        <AlertTriangle className="mt-0.5 h-5 w-5 shrink-0" aria-hidden />
        <div className="space-y-1">
          <p className="font-semibold">{title}</p>
          {message ? <p className="text-brand-rose/80">{message}</p> : null}
        </div>
      </div>
      {onRetry ? (
        <Button
          variant="outline"
          size="sm"
          className="mt-4 gap-2 border-brand-rose/40 text-brand-rose"
          onClick={onRetry}
        >
          <RefreshCw className="h-4 w-4" />
          Try again
        </Button>
      ) : null}
    </div>
  );
}
