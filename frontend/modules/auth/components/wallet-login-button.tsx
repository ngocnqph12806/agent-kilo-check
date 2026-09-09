'use client';

import { Loader2, Wallet } from 'lucide-react';
import { useRouter } from 'next/navigation';

import { Button } from '@/components/ui/button';

import { useWalletLogin } from '../hooks/use-wallet-login';

interface Props {
  /** Redirect target after a successful login (default: /discover). */
  redirectTo?: string;
}

const SHORT_ADDRESS = (addr: string) =>
  `${addr.slice(0, 6)}…${addr.slice(-4)}`;

/**
 * "Sign in with Web3 Wallet" button (Phase 1, T-M405). Triggers the full
 * SIWE flow on click — connect → sign challenge → submit → redirect.
 *
 * <p>Renders a graceful fallback when no wallet is installed (mobile
 * Safari, locked-down corporate machines) so the page never breaks.
 */
export function WalletLoginButton({ redirectTo = '/discover' }: Props) {
  const router = useRouter();
  const { step, error, login } = useWalletLogin();

  const isPending = step.kind !== 'idle';
  const label = (() => {
    switch (step.kind) {
      case 'connecting':
        return 'Opening wallet…';
      case 'signing':
        return `Sign in ${SHORT_ADDRESS(step.address)}`;
      case 'verifying':
        return `Verifying ${SHORT_ADDRESS(step.address)}…`;
      default:
        return 'Sign in with Web3 Wallet';
    }
  })();

  return (
    <div className="space-y-2">
      <Button
        type="button"
        variant="outline"
        className="h-12 w-full rounded-full border-brand-indigo text-brand-indigo hover:bg-brand-indigo/5"
        onClick={async () => {
          const ok = await login();
          if (ok) {
            router.push(redirectTo);
          }
        }}
        disabled={isPending}
      >
        {isPending ? (
          <Loader2 className="mr-2 h-4 w-4 animate-spin" aria-hidden />
        ) : (
          <Wallet className="mr-2 h-4 w-4" aria-hidden />
        )}
        {label}
      </Button>
      {error ? (
        <p className="text-xs text-destructive" role="alert">
          {error}
        </p>
      ) : (
        <p className="text-xs text-brand-muted">
          Supports MetaMask, Coinbase Wallet, Rabby, Frame and other EIP-1193 wallets.
        </p>
      )}
    </div>
  );
}
