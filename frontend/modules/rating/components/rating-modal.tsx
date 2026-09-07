'use client';

import { useEffect, useMemo, useState } from 'react';

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
  const createRating = useCreateRating();

  useEffect(() => {
    if (open) {
      setOverall(0);
      setHelpfulness(0);
      setRespectfulness(0);
      setReviewText('');
    }
  }, [open]);

  const canSubmit = useMemo(() => overall >= 1 && overall <= 5, [overall]);

  if (!open) return null;

  const submit = () => {
    if (!canSubmit) return;
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
          // eslint-disable-next-line no-alert
          alert(err.message);
        }
      }
    );
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div
        className="w-full max-w-md rounded-lg bg-white p-6 shadow-xl"
        role="dialog"
        aria-modal="true"
        aria-label="Rate session"
      >
        <header className="mb-4">
          <h2 className="text-lg font-semibold">Rate your session</h2>
          <p className="mt-1 text-sm text-muted-foreground">
            How was your session with {rateeName}?
          </p>
        </header>

        <ScorePicker label="Overall" value={overall} onChange={setOverall} />
        <ScorePicker label="Helpfulness" value={helpfulness} onChange={setHelpfulness} />
        <ScorePicker label="Respectfulness" value={respectfulness} onChange={setRespectfulness} />

        <label className="mt-4 block text-sm font-medium" htmlFor="rating-review">
          Comments (optional)
        </label>
        <textarea
          id="rating-review"
          className="mt-1 w-full rounded-md border border-zinc-200 p-2 text-sm"
          rows={3}
          value={reviewText}
          maxLength={2000}
          onChange={(e) => setReviewText(e.target.value)}
          placeholder="What did you learn? Anything that could be improved?"
        />

        <div className="mt-5 flex items-center justify-end gap-2">
          <Button variant="ghost" onClick={onClose} disabled={createRating.isPending}>
            Skip
          </Button>
          <Button onClick={submit} disabled={!canSubmit || createRating.isPending}>
            {createRating.isPending ? 'Submitting…' : 'Submit rating'}
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
}

function ScorePicker({ label, value, onChange }: ScorePickerProps) {
  return (
    <div className="mb-3">
      <p className="text-sm font-medium">{label}</p>
      <div className="mt-1 flex items-center gap-1">
        {SCORE_VALUES.map((n) => (
          <button
            key={n}
            type="button"
            aria-label={`${n} star${n > 1 ? 's' : ''}`}
            onClick={() => onChange(n)}
            className={cn(
              'h-9 w-9 rounded-md border text-sm transition-colors',
              value === n
                ? 'border-amber-400 bg-amber-50 text-amber-700'
                : 'border-zinc-200 hover:bg-zinc-50'
            )}
          >
            {n}★
          </button>
        ))}
      </div>
    </div>
  );
}
