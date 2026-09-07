'use client';

import { useEffect, useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import { useSubmitRating } from '../hooks/use-rating';
import { StarPicker } from './star-picker';

export interface RatingModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  bookingId: string;
  direction: 'learner_to_teacher' | 'teacher_to_learner';
  rateeName: string;
  onRated?: () => void;
}

const MAX_REVIEW = 500;

export function RatingModal({
  open,
  onOpenChange,
  bookingId,
  direction,
  rateeName,
  onRated
}: RatingModalProps) {
  const [overallScore, setOverallScore] = useState<number | null>(null);
  const [helpfulnessScore, setHelpfulnessScore] = useState<number | null>(null);
  const [respectfulnessScore, setRespectfulnessScore] = useState<number | null>(null);
  const [reviewText, setReviewText] = useState('');
  const submit = useSubmitRating();

  useEffect(() => {
    if (open) {
      setOverallScore(null);
      setHelpfulnessScore(null);
      setRespectfulnessScore(null);
      setReviewText('');
      submit.reset();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  if (!open) {
    return null;
  }

  const learner = direction === 'learner_to_teacher';
  const canSubmit = learner
    ? overallScore != null
    : helpfulnessScore != null && respectfulnessScore != null;

  const submitRating = async () => {
    if (!canSubmit) return;
    const input = learner
      ? {
          bookingId,
          overallScore: overallScore ?? undefined,
          reviewText: reviewText.trim() || undefined
        }
      : {
          bookingId,
          helpfulnessScore: helpfulnessScore ?? undefined,
          respectfulnessScore: respectfulnessScore ?? undefined,
          reviewText: reviewText.trim() || undefined
        };
    await submit.mutateAsync(input);
    onRated?.();
    onOpenChange(false);
  };

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-label="Rate your session"
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
      onClick={() => onOpenChange(false)}
    >
      <div
        className="w-full max-w-md space-y-4 rounded-lg border bg-card p-6 shadow-lg"
        onClick={(event) => event.stopPropagation()}
      >
        <header className="space-y-1">
          <h2 className="text-lg font-semibold">Rate your session</h2>
          <p className="text-sm text-muted-foreground">
            {learner
              ? `How was your session with ${rateeName}?`
              : `How was ${rateeName} as a learner?`}
          </p>
        </header>

        {learner ? (
          <Field label="Overall score">
            <StarPicker
              value={overallScore ?? 0}
              onChange={setOverallScore}
              ariaLabel="Overall score"
            />
          </Field>
        ) : (
          <div className="space-y-3">
            <Field label="Helpfulness">
              <StarPicker
                value={helpfulnessScore ?? 0}
                onChange={setHelpfulnessScore}
                ariaLabel="Helpfulness"
              />
            </Field>
            <Field label="Respectfulness">
              <StarPicker
                value={respectfulnessScore ?? 0}
                onChange={setRespectfulnessScore}
                ariaLabel="Respectfulness"
              />
            </Field>
          </div>
        )}

        <Field label="Notes (optional)">
          <textarea
            value={reviewText}
            onChange={(event) =>
              setReviewText(event.target.value.slice(0, MAX_REVIEW))
            }
            placeholder={
              learner
                ? 'Share what worked, what could be better…'
                : 'Anything noteworthy about the learner…'
            }
            rows={4}
            className="w-full rounded border bg-background px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
          />
          <p
            className={cn(
              'mt-1 text-right text-xs',
              reviewText.length === MAX_REVIEW
                ? 'text-amber-500'
                : 'text-muted-foreground'
            )}
          >
            {reviewText.length}/{MAX_REVIEW}
          </p>
        </Field>

        {submit.isError ? (
          <p className="rounded border border-destructive/30 bg-destructive/5 p-2 text-xs text-destructive">
            {submit.error instanceof Error
              ? submit.error.message
              : 'Could not submit rating.'}
          </p>
        ) : null}

        <div className="flex justify-end gap-2">
          <Button variant="ghost" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button
            onClick={submitRating}
            disabled={!canSubmit || submit.isPending}
          >
            {submit.isPending ? 'Submitting…' : 'Submit rating'}
          </Button>
        </div>
      </div>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="space-y-1">
      <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
        {label}
      </p>
      {children}
    </div>
  );
}
