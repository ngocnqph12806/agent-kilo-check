'use client';

import { useState } from 'react';

import { apiClient, type ApiError } from '@/lib/api-client';

import {
  getChainId,
  requestAccounts,
  signMessage,
  WalletUserRejectedError
} from '../lib/web3';
import type { AuthUserSummary } from '../lib/schemas';
import { setAccessToken } from '../lib/token-storage';
import { useAuthStore } from '../stores/auth-store';

export type WalletLoginStep =
  | { kind: 'idle' }
  | { kind: 'connecting' }
  | { kind: 'signing'; address: string }
  | { kind: 'verifying'; address: string };

interface ChallengeResponse {
  message: string;
  chainId: number;
}

interface VerifyResponse {
  accessToken: string;
  refreshToken?: string;
  expiresIn: number;
  tokenType: string;
  user: AuthUserSummary;
}

export interface UseWalletLoginResult {
  step: WalletLoginStep;
  error: string | null;
  reset: () => void;
  login: () => Promise<boolean>;
}

const ERROR_COPY: Record<string, string> = {
  UNSUPPORTED_CHAIN:
    'This chain is not supported. Switch to Ethereum mainnet or Sepolia.',
  INVALID_SIGNATURE: 'Signature could not be verified. Please retry.',
  CHALLENGE_REPLAYED:
    'This challenge has already been used. Please request a new one.',
  RATE_LIMITED: 'Too many wallet sign-in attempts. Please wait a moment.',
  WALLET_ALREADY_LINKED:
    'This wallet is already linked to a different account.'
};

/**
 * Full SIWE login flow: connect → sign challenge → submit. Updates the
 * auth store on success and returns true so the caller can redirect.
 */
export function useWalletLogin(): UseWalletLoginResult {
  const setUser = useAuthStore((s) => s.setUser);
  const [step, setStep] = useState<WalletLoginStep>({ kind: 'idle' });
  const [error, setError] = useState<string | null>(null);

  function reset() {
    setStep({ kind: 'idle' });
    setError(null);
  }

  async function login(): Promise<boolean> {
    setError(null);
    try {
      setStep({ kind: 'connecting' });
      const [address] = await requestAccounts();
      const detectedChain = await getChainId();

      setStep({ kind: 'signing', address });
      const challenge = await fetchChallenge(address, detectedChain);
      const signature = await signMessage(address, challenge.message);

      setStep({ kind: 'verifying', address });
      const verify = await submitVerify(challenge, signature, detectedChain);
      setAccessToken(verify.accessToken);
      setUser(verify.user);
      setStep({ kind: 'idle' });
      return true;
    } catch (err) {
      setStep({ kind: 'idle' });
      setError(toMessage(err));
      return false;
    }
  }

  return { step, error, reset, login };
}

async function fetchChallenge(
  address: string,
  chainId: number
): Promise<ChallengeResponse> {
  const { data } = await apiClient.post<ChallengeResponse>(
    '/auth/wallet/challenge',
    { address, chainId }
  );
  return data;
}

async function submitVerify(
  challenge: ChallengeResponse,
  signature: string,
  chainId: number
): Promise<VerifyResponse> {
  const { data } = await apiClient.post<VerifyResponse>(
    '/auth/wallet/verify',
    {
      message: challenge.message,
      signature,
      chainId
    }
  );
  return data;
}

function toMessage(err: unknown): string {
  if (err instanceof WalletUserRejectedError) {
    return 'You rejected the signature request. Please try again.';
  }
  const apiErr = err as ApiError | undefined;
  const code = apiErr?.response?.data?.code;
  if (code && ERROR_COPY[code]) return ERROR_COPY[code];
  if (err instanceof Error) return err.message;
  return 'Wallet sign-in failed.';
}
