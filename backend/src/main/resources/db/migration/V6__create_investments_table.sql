-- ============================================
-- V6: Create investments table
-- ============================================

CREATE TABLE investments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    invested_amount NUMERIC(19, 2) NOT NULL CHECK (invested_amount > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_investments_user_id ON investments(user_id);