import { useState, useEffect } from 'react';
import { getBudgets, createBudget, updateBudget, deleteBudget } from '../api/budgets.ts';
import type { BudgetInput } from '../api/budgets.ts';
import { getCategories } from '../api/categories.ts';
import type { Budget, Category } from '../types';
import './BudgetsPage.css';

const MONTHS = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
];

const now = new Date();
const CURRENT_MONTH = now.getMonth() + 1;
const CURRENT_YEAR = now.getFullYear();

function BudgetsPage() {
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);

  const [selectedMonth, setSelectedMonth] = useState(CURRENT_MONTH);
  const [selectedYear, setSelectedYear] = useState(CURRENT_YEAR);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formCategoryId, setFormCategoryId] = useState('');
  const [formLimit, setFormLimit] = useState('');

  useEffect(() => {
    loadBudgets();
  }, [selectedMonth, selectedYear]);

  useEffect(() => {
    getCategories().then(setCategories).catch(() => {});
  }, []);

  function loadBudgets() {
    setLoading(true);
    getBudgets(selectedMonth, selectedYear)
      .then(setBudgets)
      .catch(() => setBudgets([]))
      .finally(() => setLoading(false));
  }

  function formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(amount);
  }

  function openCreate() {
    setEditingId(null);
    setFormCategoryId('');
    setFormLimit('');
    setModalOpen(true);
  }

  function openEdit(budget: Budget) {
    setEditingId(budget.id);
    setFormCategoryId(budget.categoryId);
    setFormLimit(String(budget.limitAmount));
    setModalOpen(true);
  }

  async function handleSave() {
    const data: BudgetInput = {
      categoryId: formCategoryId,
      limitAmount: parseFloat(formLimit),
      month: selectedMonth,
      year: selectedYear,
    };
    try {
      if (editingId) {
        await updateBudget(editingId, data);
      } else {
        await createBudget(data);
      }
      setModalOpen(false);
      loadBudgets();
    } catch {
      alert('No se pudo guardar. ¿Quizás ya existe un presupuesto para esa categoría este mes?');
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('¿Seguro que quieres eliminar este presupuesto?')) return;
    await deleteBudget(id);
    loadBudgets();
  }

  const expenseCategories = categories.filter((c) => c.type === 'EXPENSE');
  const canSave = formCategoryId && formLimit && parseFloat(formLimit) > 0;

  function barWidth(spent: number, limit: number): number {
    if (limit <= 0) return 0;
    return Math.min((spent / limit) * 100, 100);
  }

  function barColor(exceeded: boolean, spent: number, limit: number): string {
    if (exceeded) return 'var(--oikos-expense)';
    if (limit > 0 && spent / limit >= 0.8) return '#E0A800';
    return 'var(--oikos-brand)';
  }

  // Solo año actual y siguiente (no tiene sentido presupuestar el pasado)
  const years = [CURRENT_YEAR, CURRENT_YEAR + 1];

  // Un mes está deshabilitado si es del año actual y anterior al mes actual
  function isMonthDisabled(month: number): boolean {
    return selectedYear === CURRENT_YEAR && month < CURRENT_MONTH;
  }

  return (
    <div className="budgets">
      <div className="budgets-header">
        <h1 className="budgets-title">Presupuestos</h1>
        <button
          className="btn-primary"
          onClick={openCreate}
          disabled={expenseCategories.length === 0}
        >
          + Nuevo presupuesto
        </button>
      </div>

      <div className="budget-period-selector">
        <select
          value={selectedMonth}
          onChange={(e) => setSelectedMonth(Number(e.target.value))}
        >
          {MONTHS.map((m, i) => (
            <option key={i} value={i + 1} disabled={isMonthDisabled(i + 1)}>
              {m}
            </option>
          ))}
        </select>
        <select
          value={selectedYear}
          onChange={(e) => {
            const newYear = Number(e.target.value);
            setSelectedYear(newYear);
            // Si cambias a año actual y el mes seleccionado ya pasó, salta al mes actual
            if (newYear === CURRENT_YEAR && selectedMonth < CURRENT_MONTH) {
              setSelectedMonth(CURRENT_MONTH);
            }
          }}
        >
          {years.map((y) => (
            <option key={y} value={y}>{y}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <p>Cargando...</p>
      ) : expenseCategories.length === 0 ? (
        <div className="accounts-empty">
          Necesitas al menos una categoría de gasto para crear presupuestos.
        </div>
      ) : budgets.length > 0 ? (
        <div className="budgets-list">
          {budgets.map((b) => (
            <div className={`budget-card ${b.exceeded ? 'exceeded' : ''}`} key={b.id}>
              <div className="budget-top">
                <span className="budget-category">
                  {b.categoryName}
                  {b.exceeded && <span className="budget-badge">Superado</span>}
                </span>
                <div className="budget-actions">
                  <button className="btn-icon" onClick={() => openEdit(b)}>Editar</button>
                  <button className="btn-icon danger" onClick={() => handleDelete(b.id)}>Borrar</button>
                </div>
              </div>

              <div className="budget-amounts">
                <span className="budget-spent">{formatCurrency(b.spent)}</span>
                <span className="budget-limit">de {formatCurrency(b.limitAmount)}</span>
              </div>

              <div className="budget-bar">
                <div
                  className="budget-bar-fill"
                  style={{
                    width: `${barWidth(b.spent, b.limitAmount)}%`,
                    background: barColor(b.exceeded, b.spent, b.limitAmount),
                  }}
                />
              </div>

              <div className={`budget-remaining ${b.remaining < 0 ? 'negative' : ''}`}>
                {b.remaining >= 0
                  ? `Te quedan ${formatCurrency(b.remaining)}`
                  : `Te has pasado ${formatCurrency(Math.abs(b.remaining))}`}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="accounts-empty">
          No hay presupuestos para {MONTHS[selectedMonth - 1]} {selectedYear}.
        </div>
      )}

      {modalOpen && (
        <div className="modal-overlay" onClick={() => setModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">{editingId ? 'Editar presupuesto' : 'Nuevo presupuesto'}</h2>

            <div className="form-field">
              <label htmlFor="category">Categoría</label>
              <select id="category" value={formCategoryId} onChange={(e) => setFormCategoryId(e.target.value)} disabled={!!editingId}>
                <option value="">Selecciona una categoría</option>
                {expenseCategories.map((cat) => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="limit">Límite (€) para {MONTHS[selectedMonth - 1]} {selectedYear}</label>
              <input id="limit" type="number" step="0.01" min="0" value={formLimit}
                onChange={(e) => setFormLimit(e.target.value)} placeholder="0,00" />
            </div>

            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setModalOpen(false)}>Cancelar</button>
              <button className="btn-primary" onClick={handleSave} disabled={!canSave}>
                {editingId ? 'Guardar' : 'Crear'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default BudgetsPage;