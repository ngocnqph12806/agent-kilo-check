'use client';

import { useState } from 'react';

import { cn } from '@/lib/utils';

import {
  useWalletSummary,
  useWalletTransactions
} from '../hooks/use-wallet';
import {
  WALLET_TIER_LABEL,
  type SeedTransaction,
  type SeedTransactionType,
  type WalletSummary as WalletSummaryType
} from '../lib/schemas';

const TYPE_LABEL: Record<SeedTransactionType, string> = {
  earn: 'Earned',
  spend: 'Spent',
  grant: 'Grant',
  expire: 'Expired',
  refund: 'Refund'
};

const TYPE_COLOR: Record<SeedTransactionType, string> = {
  earn: 'text-emerald-700',
  spend: 'text-rose-700',
  grant: 'text-sky-700',
  expire: 'text-zinc-500',
  refund: 'text-amber-700'
};

export function WalletView() {
  const summary = useWalletSummary();
  const [page, setPage] = useState<number>(0);
  const tx = useWalletTransactions(page, 20);

  if (summary.isLoading || !summary.data) {
    return <CenteredMessage>Loading wallet…</CenteredMessage>;
  }
  if (summary.error) {
    return (
      <CenteredMessage variant="error">
        {summary.error instanceof Error
          ? summary.error.message
          : 'Could not load wallet.'}
      </CenteredMessage>
    );
  }

  return (
    <main className="container mx-auto max-w-3xl space-y-8 py-10">
      <header className="space-y-1">
        <h1 className="text-2xl font-bold tracking-tight">Seed wallet</h1>
        <p className="text-sm text-muted-foreground">
          Track your balance, ledger, and upcoming expiry.
        </p>
      </header>

      <SummaryCards summary={summary.data} />

      <ExpiryCallout summary={summary.data} />

      <section className="space-y-3">
        <h2 className="text-base font-semibold">Transaction history</h2>
        {tx.isLoading ? (
          <p className="text-sm text-muted-foreground">Loading…</p>
        ) : tx.data && tx.data.content.length > 0 ? (
          <ul className="divide-y rounded-lg border bg-card">
            {tx.data.content.map((row) => (
              <TransactionRow key={row.id} tx={row} />
            ))}
          </ul>
        ) : (
          <p className="text-sm text-muted-foreground">
            No transactions yet.
          </p>
        )}
        {tx.data && tx.data.totalPages > 1 ? (
          <Pagination
            page={page}
            totalPages={tx.data.totalPages}
            onChange={setPage}
          />
        ) : null}
      </section>
    </main>
  );
}

function SummaryCards({ summary }: { summary: WalletSummaryType }) {
  return (
    <section className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
      <Card label="Balance" value={summary.balance} accent />
      <Card label="Total earned" value={summary.totalEarned} />
      <Card label="Total spent" value={summary.totalSpent} />
      <Card label="Tier" value={WALLET_TIER_LABEL[summary.tier]} />
    </section>
  );
}

function Card({
  label,
  value,
  accent
}: {
  label: string;
  value: number | string;
  accent?: boolean;
}) {
  return (
    <div
      className={cn(
        'rounded-lg border p-4 shadow-sm',
        accent
          ? 'border-primary bg-primary text-primary-foreground'
          : 'bg-card'
      )}
    >
      <p
        className={cn(
          'text-xs uppercase tracking-wide',
          accent ? 'text-primary-foreground/80' : 'text-muted-foreground'
        )}
      >
        {label}
      </p>
      <p className="mt-1 text-2xl font-semibold">{value}</p>
      {typeof value === 'number' ? (
        <p
          className={cn(
            'text-xs',
            accent ? 'text-primary-foreground/80' : 'text-muted-foreground'
          )}
        >
          seeds
        </p>
      ) : null}
    </div>
  );
}

function ExpiryCallout({ summary }: { summary: WalletSummaryType }) {
  if (summary.expiringSoon.amount <= 0) return null;
  const expiresAt = summary.expiringSoon.oldestExpiresAt;
  return (
    <div className="rounded-md border border-amber-300 bg-amber-50 p-3 text-sm text-amber-900">
      <p>
        <strong>{summary.expiringSoon.amount} seeds</strong> will expire
        {expiresAt ? ` by ${new Date(expiresAt).toLocaleDateString()}` : ' soon'}
        . Spend them on a session before they vanish.
      </p>
    </div>
  );
}

function TransactionRow({ tx }: { tx: SeedTransaction }) {
  return (
    <li className="flex items-start justify-between px-4 py-3 text-sm">
      <div className="space-y-1">
        <p className="font-medium">{TYPE_LABEL[tx.type]}</p>
        <p className="text-xs text-muted-foreground">
          {tx.description ?? '—'}
        </p>
        <p className="text-xs text-muted-foreground">
          {new Date(tx.createdAt).toLocaleString()}
        </p>
      </div>
      <div className="text-right">
        <p className={cn('font-semibold', TYPE_COLOR[tx.type])}>
          {tx.amount > 0 ? '+' : ''}
          {tx.amount}
        </p>
        <p className="text-xs text-muted-foreground">
          Balance: {tx.balanceAfter}
        </p>
        {tx.expiresAt ? (
          <p className="text-xs text-muted-foreground">
            Expires {new Date(tx.expiresAt).toLocaleDateString()}
          </p>
        ) : null}
      </div>
    </li>
  );
}

function Pagination({
  page,
  totalPages,
  onChange
}: {
  page: number;
  totalPages: number;
  onChange: (next: number) => void;
}) {
  return (
    <div className="flex items-center justify-end gap-2 text-xs">
      <button
        type="button"
        className="rounded border border-input px-2 py-1 disabled:opacity-50"
        onClick={() => onChange(Math.max(0, page - 1))}
        disabled={page === 0}
      >
        Previous
      </button>
      <span className="text-muted-foreground">
        Page {page + 1} of {totalPages}
      </span>
      <button
        type="button"
        className="rounded border border-input px-2 py-1 disabled:opacity-50"
        onClick={() => onChange(Math.min(totalPages - 1, page + 1))}
        disabled={page >= totalPages - 1}
      >
        Next
      </button>
    </div>
  );
}

function CenteredMessage({
  children,
  variant
}: {
  children: React.ReactNode;
  variant?: 'error';
}) {
  return (
    <main className="container mx-auto max-w-3xl py-20 text-center text-sm">
      <p
        className={cn(
          'rounded-md border p-4',
          variant === 'error'
            ? 'border-destructive/30 bg-destructive/5 text-destructive'
            : 'text-muted-foreground'
        )}
      >
        {children}
      </p>
    </main>
  );
}
