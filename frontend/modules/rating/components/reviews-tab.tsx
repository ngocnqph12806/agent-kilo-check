'use client';

import { Star } from 'lucide-react';

import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';

import { useUserRatings } from '../hooks/use-rating';
import { StarPicker } from './star-picker';

export interface ReviewsTabProps {
  userId: string;
}

export function ReviewsTab({ userId }: ReviewsTabProps) {
  const { data, isLoading, isError, error, refetch } = useUserRatings(userId);

  if (isLoading) {
    return <ReviewsSkeleton />;
  }
  if (isError) {
    return (
      <p className="rounded-md border border-destructive/30 bg-destructive/5 p-3 text-sm text-destructive">
        {error instanceof Error ? error.message : 'Could not load reviews.'}
        <button
          type="button"
          onClick={() => refetch()}
          className="ml-2 underline"
        >
          Retry
        </button>
      </p>
    );
  }
  const ratings = data?.content ?? [];
  return (
    <section className="space-y-4">
      <header className="flex flex-wrap items-baseline gap-3">
        <h2 className="text-lg font-semibold">Reviews</h2>
        {data?.averageOverallScore != null ? (
          <p className="flex items-center gap-1 text-sm text-muted-foreground">
            <Star className="h-4 w-4 fill-amber-400 text-amber-400" />
            <span className="font-medium text-foreground">
              {data.averageOverallScore.toFixed(1)}
            </span>
            <span>· {data.totalElements} review{data.totalElements === 1 ? '' : 's'}</span>
          </p>
        ) : (
          <p className="text-sm text-muted-foreground">No reviews yet.</p>
        )}
      </header>

      {ratings.length === 0 ? (
        <p className="rounded-md border border-dashed bg-muted/40 p-4 text-sm text-muted-foreground">
          Once a learner finishes a session with this user, the review will
          appear here.
        </p>
      ) : (
        <ul className="space-y-3">
          {ratings.map((rating) => (
            <li
              key={rating.id}
              className="rounded-md border bg-card p-4 text-sm shadow-sm"
            >
              <div className="flex flex-wrap items-center justify-between gap-2">
                <p className="font-medium">{rating.raterName}</p>
                <span className="text-xs text-muted-foreground">
                  {new Date(rating.createdAt).toLocaleDateString()}
                </span>
              </div>
              {rating.direction === 'learner_to_teacher' ? (
                <div className="mt-2 flex items-center gap-2">
                  <StarPicker value={rating.overallScore ?? 0} onChange={() => undefined} readOnly size="sm" />
                  <span
                    className={cn(
                      'text-xs font-medium',
                      (rating.overallScore ?? 0) >= 4
                        ? 'text-emerald-600'
                        : (rating.overallScore ?? 0) >= 3
                          ? 'text-amber-600'
                          : 'text-destructive'
                    )}
                  >
                    {rating.overallScore ?? '-'}/5
                  </span>
                </div>
              ) : (
                <div className="mt-2 flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                  <span>Helpfulness: {rating.helpfulnessScore ?? '-'}/5</span>
                  <span>Respect: {rating.respectfulnessScore ?? '-'}/5</span>
                </div>
              )}
              {rating.reviewText ? (
                <p className="mt-2 text-sm leading-relaxed">{rating.reviewText}</p>
              ) : null}
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

function ReviewsSkeleton() {
  return (
    <div className="space-y-3" aria-busy>
      <Skeleton className="h-6 w-40" />
      {Array.from({ length: 3 }).map((_, idx) => (
        <Skeleton key={idx} className="h-20 w-full" />
      ))}
    </div>
  );
}
