'use client';

import { AlertTriangle, Loader2 } from 'lucide-react';
import { useState } from 'react';

import { Button } from '@/components/ui/button';

import {
  type ReportIssueCategory,
  type ReportIssueResponse,
  reportSessionIssue
} from '../lib/session-api';

interface ReportIssueDialogProps {
  bookingId: string;
  open: boolean;
  onClose: () => void;
}

const CATEGORIES: Array<{ value: ReportIssueCategory; label: string }> = [
  { value: 'audio', label: 'Audio (mic / speaker)' },
  { value: 'video', label: 'Video (camera)' },
  { value: 'connection', label: 'Connection / lag' },
  { value: 'screen_share', label: 'Screen sharing' },
  { value: 'chat', label: 'Chat panel' },
  { value: 'other', label: 'Other' }
];

/**
 * FR-M57: "Report issue" button must open an incident ticket.
 * Posts to POST /sessions/{bookingId}/report-issue (backend handles
 * SessionIncident insert + notification).
 */
export function ReportIssueDialog({ bookingId, open, onClose }: ReportIssueDialogProps) {
  const [category, setCategory] = useState<ReportIssueCategory>('connection');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState<ReportIssueResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  if (!open) return null;

  const handleSubmit = async () => {
    if (description.trim().length < 4) {
      setError('Please describe the issue (at least a few words).');
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const result = await reportSessionIssue(bookingId, {
        category,
        description: description.trim()
      });
      setSubmitted(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not submit report.');
    } finally {
      setSubmitting(false);
    }
  };

  const reset = () => {
    setCategory('connection');
    setDescription('');
    setSubmitted(null);
    setError(null);
  };

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="report-issue-title"
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4"
    >
      <div className="w-full max-w-md space-y-4 rounded-2xl border border-[var(--brand-border)] bg-[var(--brand-surface)] p-6 shadow-brand-card">
        <header className="flex items-center gap-2">
          <AlertTriangle className="h-5 w-5 text-amber-500" aria-hidden />
          <h2 id="report-issue-title" className="text-lg font-semibold">
            Report a session issue
          </h2>
        </header>

        {submitted ? (
          <div className="space-y-3">
            <p className="text-sm">
              Thanks — incident <span className="font-mono">{submitted.incidentId.slice(0, 8)}</span>{' '}
              has been logged. Our team will review it after the session.
            </p>
            <Button type="button" variant="brand" className="w-full" onClick={() => { reset(); onClose(); }}>
              Close
            </Button>
          </div>
        ) : (
          <>
            <label className="block space-y-1 text-sm">
              <span className="font-semibold">Category</span>
              <select
                className="flex h-10 w-full rounded-xl border border-[var(--brand-border)] bg-background px-3 text-sm"
                value={category}
                onChange={(e) => setCategory(e.target.value as ReportIssueCategory)}
                disabled={submitting}
              >
                {CATEGORIES.map((c) => (
                  <option key={c.value} value={c.value}>
                    {c.label}
                  </option>
                ))}
              </select>
            </label>
            <label className="block space-y-1 text-sm">
              <span className="font-semibold">What happened?</span>
              <textarea
                className="flex w-full rounded-xl border border-[var(--brand-border)] bg-background px-3 py-2 text-sm"
                rows={4}
                placeholder="e.g. Audio dropped for ~10 seconds around the 12-minute mark."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                disabled={submitting}
                maxLength={1000}
              />
            </label>
            {error ? <p className="text-xs text-[var(--brand-rose)]">{error}</p> : null}
            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={onClose} disabled={submitting}>
                Cancel
              </Button>
              <Button type="button" variant="brand" onClick={handleSubmit} disabled={submitting}>
                {submitting ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" aria-hidden />
                    Submitting…
                  </>
                ) : (
                  'Submit report'
                )}
              </Button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
