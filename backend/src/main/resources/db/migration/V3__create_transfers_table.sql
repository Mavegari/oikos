-- ============================================
-- V3: Create transfers table
-- ============================================

CREATE TABLE transfers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    source_account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,
    target_account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    date DATE NOT NULL,
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar queries
CREATE INDEX idx_transfers_user_id ON transfers(user_id);
CREATE INDEX idx_transfers_source_account_id ON transfers(source_account_id);
CREATE INDEX idx_transfers_target_account_id ON transfers(target_account_id);
CREATE INDEX idx_transfers_date ON transfers(date);