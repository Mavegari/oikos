-- ============================================
-- V5: Create recurring_transaction_dates table (for MANUAL mode)
-- ============================================

CREATE TABLE recurring_transaction_dates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recurring_transaction_id UUID NOT NULL REFERENCES recurring_transactions(id) ON DELETE CASCADE,
    scheduled_date DATE NOT NULL,
    executed BOOLEAN NOT NULL DEFAULT FALSE,
    executed_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_recurring_transaction_dates_recurring_tx ON recurring_transaction_dates(recurring_transaction_id);
CREATE INDEX idx_recurring_transaction_dates_scheduled ON recurring_transaction_dates(scheduled_date);
CREATE INDEX idx_recurring_transaction_dates_executed ON recurring_transaction_dates(executed);