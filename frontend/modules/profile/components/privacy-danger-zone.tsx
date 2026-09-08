'use client';

import * as React from 'react';

import { Button } from '@/components/ui/button';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';

import { useDeleteMyAccount, useDownloadDataExport } from '../hooks/use-gdpr';

const RETENTION_COPY =
  'Your account will be deactivated immediately. We permanently delete your personal data 30 days later.';

export function PrivacyDangerZone() {
  const download = useDownloadDataExport();
  const remove = useDeleteMyAccount();
  const [confirmText, setConfirmText] = React.useState('');
  const [confirmOpen, setConfirmOpen] = React.useState(false);
  const expected = 'DELETE';

  const exportDisabled = download.isPending;
  const deleteDisabled = confirmText.trim() !== expected || remove.isPending;

  return (
    <section className="space-y-6 rounded-lg border border-destructive/40 p-6">
      <header className="space-y-1">
        <h2 className="text-lg font-semibold text-destructive">Danger zone</h2>
        <p className="text-sm text-muted-foreground">
          Download everything we hold on you, or permanently close your account.
        </p>
      </header>

      <div className="space-y-2">
        <h3 className="text-sm font-semibold">Download my data</h3>
        <p className="text-sm text-muted-foreground">
          We will email you a copy within 24 hours. You can also trigger an instant download below.
        </p>
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={exportDisabled}
          onClick={() => download.mutate()}
        >
          {download.isPending ? 'Preparing…' : 'Download JSON now'}
        </Button>
        {download.isError && (
          <Alert variant="destructive">
            <AlertTitle>Export failed</AlertTitle>
            <AlertDescription>{download.error.message}</AlertDescription>
          </Alert>
        )}
      </div>

      <div className="space-y-2 border-t pt-6">
        <h3 className="text-sm font-semibold">Delete my account</h3>
        <p className="text-sm text-muted-foreground">{RETENTION_COPY}</p>

        {!confirmOpen ? (
          <Button
            type="button"
            variant="destructive"
            size="sm"
            onClick={() => setConfirmOpen(true)}
          >
            Delete my account…
          </Button>
        ) : (
          <div className="space-y-3">
            <Alert variant="destructive">
              <AlertTitle>This action cannot be undone.</AlertTitle>
              <AlertDescription>
                Type <strong>{expected}</strong> in the box below to confirm.
              </AlertDescription>
            </Alert>
            <input
              type="text"
              autoFocus
              value={confirmText}
              onChange={(e) => setConfirmText(e.target.value)}
              className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              aria-label="Type DELETE to confirm"
            />
            <div className="flex gap-2">
              <Button
                type="button"
                variant="destructive"
                size="sm"
                disabled={deleteDisabled}
                onClick={() => remove.mutate()}
              >
                {remove.isPending ? 'Deleting…' : 'Permanently delete'}
              </Button>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => {
                  setConfirmOpen(false);
                  setConfirmText('');
                }}
              >
                Cancel
              </Button>
            </div>
            {remove.isError && (
              <Alert variant="destructive">
                <AlertTitle>Delete failed</AlertTitle>
                <AlertDescription>{remove.error.message}</AlertDescription>
              </Alert>
            )}
          </div>
        )}
      </div>
    </section>
  );
}