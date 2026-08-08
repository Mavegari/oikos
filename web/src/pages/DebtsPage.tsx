import { useState, useEffect } from 'react';
import { 
  getDebts, 
  createDebt, 
  updateDebt, 
  deleteDebt,
  addDebtPayment,
  deleteDebtPayment
} from '../api/debts';
import type { DebtInput, DebtPaymentInput } from '../api/debts';
import type { Debt, DebtType } from '../types';
import './DebtsPage.css';

const DEBT_TYPE_LABELS: Record<DebtType, string> = {
  I_OWE: 'Debo',
  OWED_TO_ME: 'Me deben',
};

function DebtsPage() {
  const [debts, setDebts] = useState<Debt[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formName, setFormName] = useState('');
  const [formAmount, setFormAmount] = useState<number>(0);
  const [formType, setFormType] = useState<DebtType>('I_OWE');
  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [selectedDebtId, setSelectedDebtId] = useState<string | null>(null);
  const [paymentAmount, setPaymentAmount] = useState<number>(0);
  const [paymentDate, setPaymentDate] = useState<string>(new Date().toISOString().split('T')[0]);

  useEffect(() => {
    loadDebts();
  }, []);

  async function loadDebts() {
    try {
      const data = await getDebts();
      setDebts(data);
    } catch {
      setDebts([]);
    } finally {
      setLoading(false);
    }
  }

  function formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
    }).format(amount);
  }

  function formatPercentage(value: number): string {
    return `${value.toFixed(2)}%`;
  }

  function openCreate() {
    setEditingId(null);
    setFormName('');
    setFormAmount(0);
    setFormType('I_OWE');
    setModalOpen(true);
  }

  function openEdit(debt: Debt) {
    setEditingId(debt.id);
    setFormName(debt.name);
    setFormAmount(debt.totalAmount);
    setFormType(debt.type);
    setModalOpen(true);
  }

  async function handleSave() {
    const data: DebtInput = { 
      name: formName, 
      totalAmount: formAmount,
      type: formType
    };
    
    try {
      if (editingId) {
        await updateDebt(editingId, data);
      } else {
        await createDebt(data);
      }
      setModalOpen(false);
      loadDebts();
    } catch (error) {
      alert('Error al guardar la deuda');
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('¿Seguro que quieres eliminar esta deuda?')) return;
    try {
      await deleteDebt(id);
      loadDebts();
    } catch {
      alert('Error al eliminar');
    }
  }

  async function handleAddPayment() {
    if (!selectedDebtId) return;
    
    const data: DebtPaymentInput = {
      amount: paymentAmount,
      date: paymentDate
    };

    try {
      await addDebtPayment(selectedDebtId, data);
      setPaymentModalOpen(false);
      setPaymentAmount(0);
      setPaymentDate(new Date().toISOString().split('T')[0]);
      loadDebts();
    } catch {
      alert('Error al agregar pago');
    }
  }

  async function handleDeletePayment(debtId: string, paymentId: string) {
    if (!confirm('¿Seguro que quieres eliminar este pago?')) return;
    try {
      await deleteDebtPayment(debtId, paymentId);
      loadDebts();
    } catch {
      alert('Error al eliminar pago');
    }
  }

  if (loading) {
    return <div className="debts"><p>Cargando...</p></div>;
  }

  return (
    <div className="debts">
      <div className="debts-header">
        <h1 className="debts-title">Deudas</h1>
        <button className="btn-primary" onClick={openCreate}>+ Nueva deuda</button>
      </div>

      {debts.length > 0 ? (
        <div className="debts-list">
          {debts.map((debt) => (
            <div className="debt-card" key={debt.id}>
              <div className="debt-header">
                <div>
                  <div className="debt-name">{debt.name}</div>
                  <div className="debt-type">{DEBT_TYPE_LABELS[debt.type]}</div>
                </div>
                <div className="debt-actions">
                  <button 
                    className="btn-icon" 
                    onClick={() => {
                      setSelectedDebtId(debt.id);
                      setPaymentModalOpen(true);
                    }}
                  >
                    + Pago
                  </button>
                  <button className="btn-icon" onClick={() => openEdit(debt)}>Editar</button>
                  <button className="btn-icon danger" onClick={() => handleDelete(debt.id)}>Borrar</button>
                </div>
              </div>

              <div className="debt-metrics">
                <div className="metric">
                  <span className="label">Total</span>
                  <span className="value">{formatCurrency(debt.totalAmount)}</span>
                </div>
                <div className="metric">
                  <span className="label">Pagado</span>
                  <span className="value">{formatCurrency(debt.paid)}</span>
                </div>
                <div className={`metric ${debt.pending === 0 ? 'paid' : 'pending'}`}>
                  <span className="label">Pendiente</span>
                  <span className="value">{formatCurrency(debt.pending)} ({formatPercentage(debt.pendingPercentage)})</span>
                </div>
              </div>

              {debt.payments && debt.payments.length > 0 && (
                <div className="payments-section">
                  <h4>Historial de Pagos</h4>
                  <div className="payments-list">
                    {debt.payments.map((payment) => (
                      <div className="payment-item" key={payment.id}>
                        <div>
                          <span className="payment-date">{payment.date}</span>
                          <span className="payment-amount">{formatCurrency(payment.amount)}</span>
                        </div>
                        <button 
                          className="btn-icon danger" 
                          onClick={() => handleDeletePayment(debt.id, payment.id)}
                        >
                          ✕
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="debts-empty">No tienes deudas todavía. Crea la primera con "Nueva deuda".</div>
      )}

      {modalOpen && (
        <div className="modal-overlay" onClick={() => setModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">{editingId ? 'Editar deuda' : 'Nueva deuda'}</h2>
            <div className="form-field">
              <label htmlFor="name">Nombre</label>
              <input
                id="name"
                type="text"
                value={formName}
                onChange={(e) => setFormName(e.target.value)}
                placeholder="Préstamo, Tarjeta, etc."
              />
            </div>
            <div className="form-field">
              <label htmlFor="type">Tipo</label>
              <select
                id="type"
                value={formType}
                onChange={(e) => setFormType(e.target.value as DebtType)}
              >
                <option value="I_OWE">Debo (yo)</option>
                <option value="OWED_TO_ME">Me deben</option>
              </select>
            </div>
            <div className="form-field">
              <label htmlFor="amount">Importe Total</label>
              <input
                id="amount"
                type="number"
                step="0.01"
                value={formAmount}
                onChange={(e) => setFormAmount(parseFloat(e.target.value) || 0)}
                placeholder="0.00"
              />
            </div>
            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setModalOpen(false)}>Cancelar</button>
              <button className="btn-primary" onClick={handleSave} disabled={!formName || formAmount <= 0}>
                {editingId ? 'Guardar' : 'Crear'}
              </button>
            </div>
          </div>
        </div>
      )}

      {paymentModalOpen && (
        <div className="modal-overlay" onClick={() => setPaymentModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">Registrar Pago</h2>
            <div className="form-field">
              <label htmlFor="pay-amount">Importe</label>
              <input
                id="pay-amount"
                type="number"
                step="0.01"
                value={paymentAmount}
                onChange={(e) => setPaymentAmount(parseFloat(e.target.value) || 0)}
                placeholder="0.00"
              />
            </div>
            <div className="form-field">
              <label htmlFor="pay-date">Fecha</label>
              <input
                id="pay-date"
                type="date"
                value={paymentDate}
                onChange={(e) => setPaymentDate(e.target.value)}
              />
            </div>
            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setPaymentModalOpen(false)}>Cancelar</button>
              <button className="btn-primary" onClick={handleAddPayment} disabled={paymentAmount <= 0}>Registrar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default DebtsPage;