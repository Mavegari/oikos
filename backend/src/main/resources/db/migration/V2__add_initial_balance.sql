-- ============================================
-- V2: Add initial_balance to accounts
-- ============================================

ALTER TABLE accounts
ADD COLUMN initial_balance NUMERIC(19, 2) DEFAULT 0.00 NOT NULL;

-- Comment for clarity
COMMENT ON COLUMN accounts.initial_balance IS 'Starting balance of the account. Final balance = initial_balance + SUM(income) - SUM(expense)';