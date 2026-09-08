'use client';

import { useState } from 'react';

import {
  AlertCircle,
  ArrowDownLeft,
  ArrowUpRight,
  Bell,
  CheckCircle2,
  Clock,
  CreditCard,
  Loader2,
  Sparkles,
  TrendingDown,
  TrendingUp,
  Wallet
} from 'lucide-react';

import { EmptyState } from '@/components/shared/empty-state';
import { ErrorState } from '@/components/shared/error-state';
import { LoadingState } from '@/components/shared/loading-state';
import { Button } from '@/components/ui/button';
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

const NUMBER_FORMAT = new Intl.NumberFormat('en-US');

const TIER_PILL_LABEL: Record<WalletSummaryType['tier'], string> = {
  bronze: 'Sprout',
  silver: 'Seedling',
  gold: 'Sapling',
  platinum: 'Oak'
};

const TYPE_META: Record<
  SeedTransactionType,
  {
    label: string;
    tone: 'credit' | 'debit' | 'escrow';
    Icon: typeof ArrowDownLeft;
  }
> = {
  earn: { label: 'Earned', tone: 'credit', Icon: ArrowDownLeft },
  spend: { label: 'Spent', tone: 'debit', Icon: ArrowUpRight },
  grant: { label: 'Grant', tone: 'escrow', Icon: Sparkles },
  expire: { label: 'Expired', tone: 'debit', Icon: Clock },
  refund: { label: 'Refund', tone: 'escrow', Icon: CheckCircle2 }
};

export function WalletView() {
  const summary = useWalletSummary();
  const [page, setPage] = useState<number>(0);
  const tx = useWalletTransactions(page, 20);

  if (summary.isLoading) {
    return (
      <div className="container mx-auto max-w-4xl py-10">
        <LoadingState label="Loading wallet…" rows={4} />
      </div>
    );
  }

  if (summary.error) {
    return (
      <div className="container mx-auto max-w-4xl py-10">
        <ErrorState
          title="Could not load wallet"
          message={
            summary.error instanceof Error
              ? summary.error.message
              : 'Please try again in a moment.'
          }
          onRetry={() => summary.refetch()}
        />
      </div>
    );
  }

  if (!summary.data) {
    return null;
  }

  return (
    <main className="container mx-auto max-w-4xl space-y-8 py-10">
      <header className="space-y-1">
        <h1 className="text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
          Wallet
        </h1>
        <p className="text-sm text-[var(--brand-text-muted)]">
          Track your seeds, transactions, and what&rsquo;s expiring soon.
        </p>
      </header>

      <BalanceHero summary={summary.data} />

      <SummaryRow summary={summary.data} />

      <ExpiryCallout summary={summary.data} />

      <TransactionHistory page={page} onPageChange={setPage} txQuery={tx} />
    </main>
  );
}

function BalanceHero({ summary }: { summary: WalletSummaryType }) {
  return (
    <section className="bg-brand-hero-strong text-white shadow-brand-cta rounded-3xl p-8">
      <div className="flex flex-col gap-6 sm:flex-row sm:items-start sm:justify-between">
        <div className="space-y-3">
          <p className="text-xs font-bold uppercase tracking-widest text-emerald-100">
            <Wallet className="mr-2 inline h-4 w-4" aria-hidden />
            Total balance
          </p>
          <div className="flex items-baseline gap-3">
            <span className="text-5xl font-extrabold leading-none">
              {NUMBER_FORMAT.format(summary.balance)}
            </span>
            <span className="text-lg font-semibold text-emerald-100">seeds</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="rounded-full bg-white/20 px-3 py-1 text-xs font-bold text-white">
              <Sparkles className="mr-1 inline h-3 w-3" aria-hidden />
              {TIER_PILL_LABEL[summary.tier]}
            </span>
            <span className="text-xs text-emerald-100">
              {WALLET_TIER_LABEL[summary.tier]} tier
            </span>
          </div>
        </div>

        <div className="flex flex-col gap-2 sm:items-end">
          <Button
            type="button"
            variant="brand-outline"
            className="gap-2 rounded-full"
          >
            <CreditCard className="h-4 w-4" aria-hidden />
            Buy seeds
          </Button>
          <Button
            type="button"
            variant="outline"
            className="gap-2 rounded-full border-white/40 bg-white/10 text-white hover:bg-white/20"
          >
            <ArrowUpRight className="h-4 w-4" aria-hidden />
            Send / Gift
          </Button>
        </div>
      </div>
    </section>
  );
}

function SummaryRow({ summary }: { summary: WalletSummaryType }) {
  return (
    <section className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
      <StatCard
        label="Total earned"
        value={summary.totalEarned}
        Icon={TrendingUp}
        tone="credit"
      />
      <StatCard
        label="Total spent"
        value={summary.totalSpent}
        Icon={TrendingDown}
        tone="debit"
      />
      <StatCard
        label="Tier"
        value={WALLET_TIER_LABEL[summary.tier]}
        Icon={Sparkles}
        tone="neutral"
        suffix=""
      />
      <StatCard
        label="Total expired"
        value={summary.totalExpired}
        Icon={Clock}
        tone="neutral"
      />
    </section>
  );
}

function StatCard({
  label,
  value,
  Icon,
  tone,
  suffix = 'seeds'
}: {
  label: string;
  value: number | string;
  Icon: typeof ArrowDownLeft;
  tone: 'credit' | 'debit' | 'neutral';
  suffix?: string;
}) {
  const toneStyles =
    tone === 'credit'
      ? 'bg-brand-credit-bg text-brand-credit'
      : tone === 'debit'
        ? 'bg-brand-debit-bg text-brand-debit'
        : 'bg-brand-pending-bg text-brand-pending';

  return (
    <div className="rounded-2xl border border-[var(--brand-border)] bg-white p-5 shadow-brand-card">
      <div className="flex items-center gap-3">
        <span
          className={cn(
            'flex h-10 w-10 items-center justify-center rounded-full',
            toneStyles
          )}
        >
          <Icon className="h-5 w-5" aria-hidden />
        </span>
        <p className="text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-muted)]">
          {label}
        </p>
      </div>
      <p className="mt-3 text-2xl font-extrabold text-[var(--brand-text-strong)]">
        {typeof value === 'number' ? NUMBER_FORMAT.format(value) : value}
      </p>
      {suffix ? (
        <p className="text-xs text-[var(--brand-text-muted)]">{suffix}</p>
      ) : null}
    </div>
  );
}

function ExpiryCallout({ summary }: { summary: WalletSummaryType }) {
  if (summary.expiringSoon.amount <= 0) return null;

  const expiresAt = summary.expiringSoon.oldestExpiresAt;
  const expiryDate = expiresAt ? new Date(expiresAt) : null;
  const expiryLabel = expiryDate
    ? expiryDate.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      })
    : 'soon';

  return (
    <section className="rounded-2xl border border-brand-pending bg-brand-pending-bg p-5 shadow-brand-card">
      <div className="flex items-start gap-3">
        <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-brand-pending text-white">
          <AlertCircle className="h-5 w-5" aria-hidden />
        </span>
        <div className="space-y-1">
          <p className="text-sm font-bold text-brand-pending">
            <strong>{NUMBER_FORMAT.format(summary.expiringSoon.amount)} seeds</strong>{' '}
            expiring by {expiryLabel}
          </p>
          <p className="text-xs text-amber-800">
            Book a session or gift them to a friend before they vanish.
          </p>
        </div>
      </div>
    </section>
  );
}

function TransactionHistory({
  page,
  onPageChange,
  txQuery
}: {
  page: number;
  onPageChange: (next: number) => void;
  txQuery: ReturnType<typeof useWalletTransactions>;
}) {
  const { data, isLoading, isError, error, refetch, isFetching } = txQuery;

  const totalElements = data?.totalElements ?? 0;
  const totalPages = data?.totalPages ?? 0;
  const items = data?.content ?? [];

  return (
    <section className="rounded-2xl border border-[var(--brand-border)] bg-white p-6 shadow-brand-card">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-extrabold text-[var(--brand-text-strong)]">
            Recent activity
          </h2>
          <p className="text-xs text-[var(--brand-text-muted)]">
            {NUMBER_FORMAT.format(totalElements)}{' '}
            {totalElements === 1 ? 'transaction' : 'transactions'}
          </p>
        </div>
        {isFetching && !isLoading ? (
          <Loader2 className="h-4 w-4 animate-spin text-[var(--brand-text-muted)]" aria-hidden />
        ) : null}
      </div>

      <div className="mt-5">
        {isLoading ? (
          <LoadingState label="Loading transactions…" rows={4} className="border-none p-0 shadow-none" />
        ) : isError ? (
          <ErrorState
            title="Could not load transactions"
            message={error instanceof Error ? error.message : undefined}
            onRetry={() => refetch()}
          />
        ) : items.length === 0 ? (
          <EmptyState
            emoji="💰"
            icon={Wallet}
            title="No transactions yet"
            description="Your transactions will appear here once you earn or spend your first seeds."
            action={{ label: 'Browse teachers', href: '/discover' }}
          />
        ) : (
          <ul className="divide-y divide-[var(--brand-border)]">
            {items.map((row) => (
              <TransactionRow key={row.id} tx={row} />
            ))}
          </ul>
        )}
      </div>

      {totalPages > 1 ? (
        <Pagination
          page={page}
          totalPages={totalPages}
          onChange={onPageChange}
        />
      ) : null}
    </section>
  );
}

function TransactionRow({ tx }: { tx: SeedTransaction }) {
  const meta = TYPE_META[tx.type];
  const Icon = meta.Icon;
  const isCredit = tx.amount > 0;

  const toneStyles =
    meta.tone === 'credit'
      ? 'bg-brand-credit-bg text-brand-credit'
      : meta.tone === 'debit'
        ? 'bg-brand-debit-bg text-brand-debit'
        : 'bg-brand-pending-bg text-brand-pending';

  const amountStyles = isCredit ? 'text-emerald-700' : 'text-rose-700';

  return (
    <li className="flex items-center gap-4 py-3">
      <span
        className={cn(
          'flex h-10 w-10 shrink-0 items-center justify-center rounded-full',
          toneStyles
        )}
      >
        <Icon className="h-5 w-5" aria-hidden />
      </span>
      <div className="min-w-0 flex-1">
        <p className="truncate text-sm font-semibold text-[var(--brand-text-strong)]">
          {tx.description ?? meta.label}
        </p>
        <p className="text-xs text-[var(--brand-text-muted)]">
          {meta.label} ·{' '}
          {new Date(tx.createdAt).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
          })}
        </p>
      </div>
      <div className="text-right">
        <p className={cn('text-sm font-bold', amountStyles)}>
          {isCredit ? '+' : ''}
          {NUMBER_FORMAT.format(tx.amount)} seeds
        </p>
        <p className="text-xs text-[var(--brand-text-muted)]">
          Balance {NUMBER_FORMAT.format(tx.balanceAfter)}
        </p>
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
    <div className="mt-5 flex items-center justify-end gap-2 text-xs">
      <Button
        type="button"
        variant="outline"
        size="sm"
        className="rounded-full"
        onClick={() => onChange(Math.max(0, page - 1))}
        disabled={page === 0}
      >
        Previous
      </Button>
      <span className="px-2 text-[var(--brand-text-muted)]">
        Page {page + 1} of {totalPages}
      </span>
      <Button
        type="button"
        variant="outline"
        size="sm"
        className="rounded-full"
        onClick={() => onChange(Math.min(totalPages - 1, page + 1))}
        disabled={page >= totalPages - 1}
      >
        Next
      </Button>
    </div>
  );
}
