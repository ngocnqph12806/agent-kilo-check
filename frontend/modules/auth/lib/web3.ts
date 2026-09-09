/**
 * Minimal EIP-1193 wallet adapter for SIWE (Sign-In with Ethereum).
 *
 * <p>Uses the global {@code window.ethereum} provider exposed by MetaMask,
 * Coinbase Wallet, Rabby, Frame, etc. — no wagmi/viem deps required for
 * the Phase 1 happy path. Returns null when no provider is available
 * (e.g. mobile Safari) so the caller can render a graceful fallback.
 */

export interface EthereumProvider {
  request: (args: { method: string; params?: unknown[] }) => Promise<unknown>;
  isMetaMask?: boolean;
  on?: (event: string, handler: (...args: unknown[]) => void) => void;
  removeListener?: (event: string, handler: (...args: unknown[]) => void) => void;
}

export interface EthereumProviderInfo {
  provider: EthereumProvider;
  isMetaMask: boolean;
}

declare global {
  interface Window {
    ethereum?: EthereumProvider | EthereumProvider[];
  }
}

/** Picks the first EIP-1193 provider, or returns null. */
export function getEthereumProvider(): EthereumProviderInfo | null {
  if (typeof window === 'undefined') return null;
  const candidate = window.ethereum;
  if (!candidate) return null;
  // Some wallets expose an array (e.g. multiple installed). Pick the first.
  const provider: EthereumProvider = Array.isArray(candidate) ? candidate[0] : candidate;
  if (!provider || typeof provider.request !== 'function') return null;
  return { provider, isMetaMask: Boolean(provider.isMetaMask) };
}

/** Thrown when the user rejects the signature prompt. */
export class WalletUserRejectedError extends Error {
  constructor() {
    super('Wallet signature was rejected');
    this.name = 'WalletUserRejectedError';
  }
}

/**
 * Requests accounts and returns the active address. Triggers a
 * MetaMask-style connect prompt if no account is connected yet.
 */
export async function requestAccounts(): Promise<string[]> {
  const info = getEthereumProvider();
  if (!info) {
    throw new Error('No Ethereum wallet detected. Install MetaMask or another Web3 wallet.');
  }
  const result = await info.provider.request({
    method: 'eth_requestAccounts'
  });
  if (!Array.isArray(result) || result.length === 0) {
    throw new Error('No account returned by wallet');
  }
  return result as string[];
}

/** Returns the current chain id (hex string → number). Defaults to 1 if absent. */
export async function getChainId(): Promise<number> {
  const info = getEthereumProvider();
  if (!info) return 1;
  try {
    const hex = (await info.provider.request({ method: 'eth_chainId' })) as string;
    return Number.parseInt(hex, 16);
  } catch {
    return 1;
  }
}

/**
 * Requests a personal_sign signature for the given SIWE message.
 * Wallet implementations vary on param order (some expect [address, message],
 * some [message, address]); we send [message, address] which is the
 * de-facto MetaMask convention and is also what EIP-4361 recommends.
 */
export async function signMessage(address: string, message: string): Promise<string> {
  const info = getEthereumProvider();
  if (!info) {
    throw new Error('No Ethereum wallet detected');
  }
  try {
    const signature = await info.provider.request({
      method: 'personal_sign',
      params: [message, address]
    });
    if (typeof signature !== 'string') {
      throw new Error('Wallet returned a non-string signature');
    }
    return signature;
  } catch (err) {
    if (err instanceof Error && /reject|denied/i.test(err.message)) {
      throw new WalletUserRejectedError();
    }
    throw err;
  }
}
