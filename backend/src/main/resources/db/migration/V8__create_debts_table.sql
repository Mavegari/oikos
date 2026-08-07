-- ============================================
-- V8: Create debts table
-- ============================================

CREATE TABLE debts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL CHECK (total_amount > 0),
    type VARCHAR(20) NOT NULL CHECK (type IN ('I_OWE', 'OWED_TO_ME')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_debts_user_id ON debts(user_id);
CREATE INDEX idx_debts_type ON debts(type);