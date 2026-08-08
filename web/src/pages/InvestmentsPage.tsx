import { useState, useEffect } from 'react';
import { 
  getInvestments, 
  createInvestment, 
  updateInvestment, 
  deleteInvestment,
  addInvestmentValuation,
  deleteInvestmentValuation
} from '../api/investments';
import type { InvestmentInput, InvestmentValuationInput } from '../api/investments';
import type { Investment } from '../types';
import './InvestmentsPage.css';

function InvestmentsPage() {
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formName, setFormName] = useState('');
  const [formAmount, setFormAmount] = useState<number>(0);
  const [valuationModalOpen, setValuationModalOpen] = useState(false);
  const [selectedInvestmentId, setSelectedInvestmentId] = useState<string | null>(null);
  const [valuationValue, setValuationValue] = useState<number>(0);
  const [valuationDate, setValuationDate] = useState<string>(new Date().toISOString().split('T')[0]);

  useEffect(() => {
    loadInvestments();
  }, []);

  async function loadInvestments() {
    try {
      const data = await getInvestments();
      setInvestments(data);
    } catch {
      setInvestments([]);
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
    setModalOpen(true);
  }

  function openEdit(investment: Investment) {
    setEditingId(investment.id);
    setFormName(investment.name);
    setFormAmount(investment.investedAmount);
    setModalOpen(true);
  }

  async function handleSave() {
    const data: InvestmentInput = { 
      name: formName, 
      investedAmount: formAmount 
    };
    
    try {
      if (editingId) {
        await updateInvestment(editingId, data);
      } else {
        await createInvestment(data);
      }
      setModalOpen(false);
      loadInvestments();
    } catch (error) {
      alert('Error al guardar la inversión');
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('¿Seguro que quieres eliminar esta inversión?')) return;
    try {
      await deleteInvestment(id);
      loadInvestments();
    } catch {
      alert('Error al eliminar');
    }
  }

  async function handleAddValuation() {
    if (!selectedInvestmentId) return;
    
    const data: InvestmentValuationInput = {
      value: valuationValue,
      date: valuationDate
    };

    try {
      await addInvestmentValuation(selectedInvestmentId, data);
      setValuationModalOpen(false);
      setValuationValue(0);
      setValuationDate(new Date().toISOString().split('T')[0]);
      loadInvestments();
    } catch {
      alert('Error al agregar valuación');
    }
  }

  async function handleDeleteValuation(investmentId: string, valuationId: string) {
    if (!confirm('¿Seguro que quieres eliminar esta valuación?')) return;
    try {
      await deleteInvestmentValuation(investmentId, valuationId);
      loadInvestments();
    } catch {
      alert('Error al eliminar valuación');
    }
  }

  if (loading) {
    return <div className="investments"><p>Cargando...</p></div>;
  }

  return (
    <div className="investments">
      <div className="investments-header">
        <h1 className="investments-title">Inversiones</h1>
        <button className="btn-primary" onClick={openCreate}>+ Nueva inversión</button>
      </div>

      {investments.length > 0 ? (
        <div className="investments-list">
          {investments.map((investment) => (
            <div className="investment-card" key={investment.id}>
              <div className="investment-header">
                <div>
                  <div className="investment-name">{investment.name}</div>
                  <div className="investment-invested">
                    Invertido: {formatCurrency(investment.investedAmount)}
                  </div>
                </div>
                <div className="investment-actions">
                  <button 
                    className="btn-icon" 
                    onClick={() => {
                      setSelectedInvestmentId(investment.id);
                      setValuationModalOpen(true);
                    }}
                  >
                    + Valuación
                  </button>
                  <button className="btn-icon" onClick={() => openEdit(investment)}>Editar</button>
                  <button className="btn-icon danger" onClick={() => handleDelete(investment.id)}>Borrar</button>
                </div>
              </div>

              <div className="investment-metrics">
                <div className="metric">
                  <span className="label">Valor Actual</span>
                  <span className="value">{formatCurrency(investment.currentValue)}</span>
                </div>
                <div className={`metric ${investment.profitLoss >= 0 ? 'positive' : 'negative'}`}>
                  <span className="label">Ganancia/Pérdida</span>
                  <span className="value">
                    {formatCurrency(investment.profitLoss)} ({formatPercentage(investment.profitLossPercentage)})
                  </span>
                </div>
              </div>

              {investment.valuations && investment.valuations.length > 0 && (
                <div className="valuations-section">
                  <h4>Historial de Valuaciones</h4>
                  <div className="valuations-list">
                    {investment.valuations.map((val) => (
                      <div className="valuation-item" key={val.id}>
                        <div>
                          <span className="valuation-date">{val.date}</span>
                          <span className="valuation-value">{formatCurrency(val.value)}</span>
                        </div>
                        <button 
                          className="btn-icon danger" 
                          onClick={() => handleDeleteValuation(investment.id, val.id)}
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
        <div className="investments-empty">No tienes inversiones todavía. Crea la primera con "Nueva inversión".</div>
      )}

      {modalOpen && (
        <div className="modal-overlay" onClick={() => setModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">{editingId ? 'Editar inversión' : 'Nueva inversión'}</h2>
            <div className="form-field">
              <label htmlFor="name">Nombre</label>
              <input
                id="name"
                type="text"
                value={formName}
                onChange={(e) => setFormName(e.target.value)}
                placeholder="AAPL, Fondo, etc."
              />
            </div>
            <div className="form-field">
              <label htmlFor="amount">Importe Invertido</label>
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

      {valuationModalOpen && (
        <div className="modal-overlay" onClick={() => setValuationModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">Agregar Valuación</h2>
            <div className="form-field">
              <label htmlFor="val-value">Valor</label>
              <input
                id="val-value"
                type="number"
                step="0.01"
                value={valuationValue}
                onChange={(e) => setValuationValue(parseFloat(e.target.value) || 0)}
                placeholder="0.00"
              />
            </div>
            <div className="form-field">
              <label htmlFor="val-date">Fecha</label>
              <input
                id="val-date"
                type="date"
                value={valuationDate}
                onChange={(e) => setValuationDate(e.target.value)}
              />
            </div>
            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setValuationModalOpen(false)}>Cancelar</button>
              <button className="btn-primary" onClick={handleAddValuation} disabled={valuationValue <= 0}>Agregar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default InvestmentsPage;