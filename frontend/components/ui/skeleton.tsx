import { cn } from '@/lib/utils';

function Skeleton({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn('animate-pulse rounded-md bg-muted', className)}
      role="status"
      aria-label="Loading"
      {...props}
    />
  );
}

export { Skeleton };

export interface SkeletonTextProps {
  lines?: number;
  className?: string;
  lastLineWidth?: string;
}

export function SkeletonText({ lines = 3, className, lastLineWidth = 'w-3/4' }: SkeletonTextProps) {
  return (
    <div className={cn('space-y-2', className)}>
      {Array.from({ length: lines }).map((_, idx) => (
        <Skeleton
          key={idx}
          className={cn('h-3 w-full', idx === lines - 1 && lastLineWidth)}
        />
      ))}
    </div>
  );
}

export interface SkeletonCardProps {
  className?: string;
  showAvatar?: boolean;
  showImage?: boolean;
}

export function SkeletonCard({ className, showAvatar = true, showImage = false }: SkeletonCardProps) {
  return (
    <div className={cn('rounded-lg border bg-card p-6 shadow-sm', className)}>
      {showImage && <Skeleton className="mb-4 h-32 w-full" />}
      <div className="flex items-start gap-3">
        {showAvatar && <Skeleton className="h-10 w-10 rounded-full" />}
        <div className="flex-1 space-y-2">
          <Skeleton className="h-4 w-1/2" />
          <Skeleton className="h-3 w-1/3" />
        </div>
      </div>
      <SkeletonText lines={3} className="mt-4" />
    </div>
  );
}

export interface SkeletonListProps {
  count?: number;
  className?: string;
  itemClassName?: string;
  showAvatar?: boolean;
}

export function SkeletonList({
  count = 3,
  className,
  itemClassName,
  showAvatar = true
}: SkeletonListProps) {
  return (
    <div className={cn('space-y-3', className)}>
      {Array.from({ length: count }).map((_, idx) => (
        <div
          key={idx}
          className={cn('flex items-start gap-3 rounded-md border p-4', itemClassName)}
        >
          {showAvatar && <Skeleton className="h-10 w-10 rounded-full" />}
          <div className="flex-1 space-y-2">
            <Skeleton className="h-4 w-1/3" />
            <Skeleton className="h-3 w-2/3" />
          </div>
        </div>
      ))}
    </div>
  );
}