-- ============================================
-- V4: Create recurring_transactions table
-- ============================================

CREATE TABLE recurring_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    
    -- Modo: PATTERN o MANUAL
    recurrence_mode VARCHAR(20) NOT NULL CHECK (recurrence_mode IN ('PATTERN', 'MANUAL')),
    
    -- Solo para PATTERN mode
    frequency VARCHAR(20) CHECK (frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
    interval INTEGER DEFAULT 1 CHECK (interval > 0 OR interval IS NULL),
    day_of_month INTEGER CHECK (day_of_month BETWEEN 1 AND 31 OR day_of_month IS NULL),
    day_of_week VARCHAR(10) CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') OR day_of_week IS NULL),
    
    -- Ciclo temporal
    start_date DATE,
    end_date DATE CHECK (end_date IS NULL OR end_date >= start_date),
    
    -- Próxima ejecución (solo para PATTERN mode)
    next_run_date DATE,
    
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_recurring_transactions_user_id ON recurring_transactions(user_id);
CREATE INDEX idx_recurring_transactions_account_id ON recurring_transactions(account_id);
CREATE INDEX idx_recurring_transactions_category_id ON recurring_transactions(category_id);
CREATE INDEX idx_recurring_transactions_next_run_date ON recurring_transactions(next_run_date);
CREATE INDEX idx_recurring_transactions_active ON recurring_transactions(active);