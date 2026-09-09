-- =============================================================================
-- V16__user_wallets.sql
-- Adds the `user_wallets` table for Sign-In with Ethereum (SIWE, EIP-4361)
-- authentication (Phase 1, T-M405). A user can own 0..N wallet addresses;
-- each wallet may be linked to at most one user (enforced by UNIQUE on
-- lowercased address). The `chain_id` column supports future multi-chain
-- expansion (Polygon/Arbitrum/Optimism) but we only authenticate against
-- Ethereum mainnet (1) and Sepolia (11155111) in Phase 1.
-- =============================================================================

CREATE TABLE user_wallets (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    address         VARCHAR(42) NOT NULL,
    address_lower   VARCHAR(42) NOT NULL,
    chain_id        BIGINT NOT NULL,
    ens_name        VARCHAR(255),
    is_primary      BOOLEAN NOT NULL DEFAULT FALSE,
    linked_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    last_used_at    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_user_wallets_address_lower UNIQUE (address_lower),
    CONSTRAINT chk_user_wallets_chain CHECK (chain_id IN (1, 11155111))
);

CREATE INDEX idx_user_wallets_user_id ON user_wallets(user_id);
CREATE INDEX idx_user_wallets_primary ON user_wallets(user_id) WHERE is_primary = TRUE;
