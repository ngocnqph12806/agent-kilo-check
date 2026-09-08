# ADR-003: Append-only ledger pattern for the Seed wallet

- **Status:** Accepted
- **Date:** 2026-08-20
- **Deciders:** Founder, Tech Lead
- **Related:** `SKILLSEED.md` §11, `docs/STATE_MACHINES.md`

## Context

The Seed wallet holds the user's balance and movement history. We must:

- Never lose money (no negative balances unless explicitly overdrafting).
- Reconcile total supply vs outstanding balance at any moment.
- Replay the history of any user for auditing and GDPR export.
- Allow 12-month expiry on credits without complex background sweeps.

A "naive balance column" doesn't satisfy (3) or (4). A double-entry
ledger is overkill for Phase 1.

## Decision

Adopt an **append-only ledger** pattern:

- `seed_wallets` row holds the **current cached** balance.
- `seed_transaction` rows are **insert-only** — never updated or
  deleted (except for soft status flips like `EXPIRED`).
- Every balance change is a pair of: write `seed_transaction` row →
  update `balance_cached` in the same transaction.
- Background job `processSeedExpiry` flips status to `EXPIRED` for
  rows older than 12 months (does not delete).

We pair it with a daily reconciliation job (`[jobs] wallet drift = 0`)
that asserts:

```
SUM(balance_cached) == SUM(amount WHERE status='COMPLETED')
```

## Consequences

**Positive**
- Complete history for every Seed movement.
- Easy export for GDPR (T-M201) — just dump the rows.
- Drift detection catches bugs before users notice.
- Expiry is reversible (we re-credit if needed) because we never delete
  rows.

**Negative**
- Storage grows ~forever — mitigated by partition + archive after
  24 months (Phase 2).
- Concurrent updates need a `SELECT … FOR UPDATE` on the wallet row.
- Reconciliation job has to handle clock skew (we tolerate ±1 Seed).

**Reversible?** No — data is on disk forever. But the schema is
extensible if we later need true double-entry.