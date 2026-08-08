-- ============================================
-- V10: Add recurring flag to budgets table
-- ============================================

ALTER TABLE budgets
ADD COLUMN recurring BOOLEAN NOT NULL DEFAULT false;

-- Índice para búsquedas rápidas de presupuestos recurrentes
CREATE INDEX idx_budgets_recurring ON budgets(recurring);