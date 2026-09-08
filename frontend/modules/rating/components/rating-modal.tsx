'use client';

import { useEffect, useMemo, useState } from 'react';
import { Star, Loader2, X } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import { useCreateRating } from '../hooks/use-ratings';

export interface RatingModalProps {
  open: boolean;
  bookingId: string;
  rateeName: string;
  onClose: () => void;
  onSubmitted?: () => void;
}

const SCORE_VALUES: number[] = [1, 2, 3, 4, 5];

export function RatingModal({ open, bookingId, rateeName, onClose, onSubmitted }: RatingModalProps) {
  const [overall, setOverall] = useState<number>(0);
  const [helpfulness, setHelpfulness] = useState<number>(0);
  const [respectfulness, setRespectfulness] = useState<number>(0);
  const [reviewText, setReviewText] = useState('');
  const [error, setError] = useState<string | null>(null);
  const createRating = useCreateRating();

  useEffect(() => {
    if (open) {
      setOverall(0);
      setHelpfulness(0);
      setRespectfulness(0);
      setReviewText('');
      setError(null);
    }
  }, [open]);

  const canSubmit = useMemo(() => overall >= 1 && overall <= 5, [overall]);

  if (!open) return null;

  const submit = () => {
    if (!canSubmit) return;
    setError(null);
    createRating.mutate(
      {
        bookingId,
        overallScore: overall,
        reviewText: reviewText.trim() || undefined,
        helpfulnessScore: helpfulness || undefined,
        respectfulnessScore: respectfulness || undefined
      },
      {
        onSuccess: () => {
          onSubmitted?.();
          onClose();
        },
        onError: (err) => {
          setError(err instanceof Error ? err.message : 'Could not submit rating.');
        }
      }
    );
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
      <div
        className="relative w-full max-w-md rounded-2xl border border-[var(--brand-border)] bg-white p-8 shadow-brand-card"
        role="dialog"
        aria-modal="true"
        aria-label="Rate session"
      >
        <button
          type="button"
          onClick={onClose}
          aria-label="Close"
          className="absolute right-3 top-3 inline-flex h-8 w-8 items-center justify-center rounded-full text-[var(--brand-text-muted)] transition hover:bg-[var(--brand-surface)] hover:text-[var(--brand-text-strong)]"
        >
          <X className="h-4 w-4" />
        </button>

        <header className="mb-6 text-center">
          <div className="mx-auto mb-3 inline-flex h-12 w-12 items-center justify-center rounded-full bg-[var(--brand-hero-soft)] text-primary">
            <Star className="h-6 w-6 fill-primary text-primary" />
          </div>
          <h2 className="text-xl font-bold text-[var(--brand-text-strong)]">Rate your session</h2>
          <p className="mt-1 text-sm text-[var(--brand-text-muted)]">
            How was your session with <span className="font-semibold text-[var(--brand-text-strong)]">{rateeName}</span>?
          </p>
        </header>

        <ScorePicker label="Overall" value={overall} onChange={setOverall} required />
        <ScorePicker label="Helpfulness" value={helpfulness} onChange={setHelpfulness} />
        <ScorePicker label="Respectfulness" value={respectfulness} onChange={setRespectfulness} />

        <label className="mt-4 block text-sm font-medium" htmlFor="rating-review">
          Comments (optional)
        </label>
        <textarea
          id="rating-review"
          className="mt-1 w-full rounded-md border border-[var(--brand-border)] bg-white p-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
          rows={3}
          value={reviewText}
          maxLength={2000}
          onChange={(e) => setReviewText(e.target.value)}
          placeholder="What did you learn? Anything that could be improved?"
        />

        {error ? (
          <p className="mt-3 rounded-md border border-[var(--brand-rose)]/30 bg-[var(--brand-rose)]/5 p-2 text-sm text-[var(--brand-rose)]">
            {error}
          </p>
        ) : null}

        <div className="mt-6 flex items-center justify-end gap-2">
          <Button type="button" variant="ghost" onClick={onClose} disabled={createRating.isPending}>
            Skip
          </Button>
          <Button type="button"
            onClick={submit}
            disabled={!canSubmit || createRating.isPending}
            variant="brand" className="h-11 rounded-full px-6 font-semibold"
          >
            {createRating.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Submitting…
              </>
            ) : (
              'Submit rating'
            )}
          </Button>
        </div>
      </div>
    </div>
  );
}

interface ScorePickerProps {
  label: string;
  value: number;
  onChange: (n: number) => void;
  required?: boolean;
}

function ScorePicker({ label, value, onChange, required }: ScorePickerProps) {
  return (
    <div className="mb-3">
      <p className="text-sm font-medium text-[var(--brand-text-strong)]">
        {label} {required ? <span className="text-[var(--brand-rose)]">*</span> : null}
      </p>
      <div className="mt-1 flex items-center gap-1">
        {SCORE_VALUES.map((n) => {
          const active = value >= n;
          return (
            <button
              key={n}
              type="button"
              aria-label={`${n} star${n > 1 ? 's' : ''}`}
              onClick={() => onChange(n)}
              className={cn(
                'rounded-md p-1.5 transition hover:bg-amber-50',
                active ? 'text-amber-500' : 'text-zinc-300'
              )}
            >
              <Star className={cn('h-7 w-7', active && 'fill-current')} />
            </button>
          );
        })}
      </div>
    </div>
  );
}
