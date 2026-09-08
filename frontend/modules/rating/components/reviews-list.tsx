'use client';

import { useState } from 'react';

import { Button } from '@/components/ui/button';

import { useUserRatings } from '../hooks/use-ratings';

interface ReviewsListProps {
  userId: string;
}

export function ReviewsList({ userId }: ReviewsListProps) {
  const [page, setPage] = useState(0);
  const { data, isLoading, error } = useUserRatings(userId, page, 10);

  if (isLoading) {
    return <p className="text-sm text-muted-foreground">Loading reviews…</p>;
  }
  if (error) {
    return (
      <p className="text-sm text-destructive">
        Could not load reviews: {error instanceof Error ? error.message : 'Unknown error'}
      </p>
    );
  }
  if (!data || data.items.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">
        No reviews yet — completed sessions with feedback will show up here.
      </p>
    );
  }

  return (
    <div className="space-y-3">
      {data.items.map((rating) => (
        <article
          key={rating.id}
          className="rounded-lg border border-zinc-200 bg-card p-4"
        >
          <header className="flex items-center justify-between text-sm">
            <div>
              <p className="font-medium">{rating.raterName ?? 'Anonymous'}</p>
              <p className="text-xs text-muted-foreground">
                {new Date(rating.createdAt).toLocaleDateString()}
              </p>
            </div>
            <div className="flex items-center gap-1 text-amber-500" aria-label={`${rating.overallScore} stars`}>
              {Array.from({ length: 5 }, (_, i) => (
                <span key={i}>{i < rating.overallScore ? '★' : '☆'}</span>
              ))}
            </div>
          </header>
          {rating.reviewText ? (
            <p className="mt-2 whitespace-pre-line text-sm text-zinc-700">
              {rating.reviewText}
            </p>
          ) : (
            <p className="mt-2 text-xs italic text-muted-foreground">
              {rating.autoRated ? 'Auto-rated (no manual review)' : 'No written review'}
            </p>
          )}
        </article>
      ))}

      <div className="flex items-center justify-between pt-2">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          disabled={page === 0}
        >
          Previous
        </Button>
        <p className="text-xs text-muted-foreground">
          Page {page + 1} of {data.totalPages}
        </p>
        <Button
          variant="ghost"
          size="sm"
          onClick={() => setPage((p) => p + 1)}
          disabled={page + 1 >= data.totalPages}
        >
          Next
        </Button>
      </div>
    </div>
  );
}
