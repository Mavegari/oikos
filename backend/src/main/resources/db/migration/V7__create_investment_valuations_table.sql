-- ============================================
-- V7: Create investment_valuations table
-- ============================================

CREATE TABLE investment_valuations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    investment_id UUID NOT NULL REFERENCES investments(id) ON DELETE CASCADE,
    value NUMERIC(19, 2) NOT NULL CHECK (value > 0),
    date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_investment_valuations_investment_id ON investment_valuations(investment_id);
CREATE INDEX idx_investment_valuations_date ON investment_valuations(date);