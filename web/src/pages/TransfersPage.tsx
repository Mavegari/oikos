import { useState, useEffect } from 'react';
import { 
  getTransfers, 
  createTransfer, 
  updateTransfer, 
  deleteTransfer
} from '../api/transfers';
import type { TransferInput } from '../api/transfers';
import type { Transfer, Account } from '../types';
import { getAccounts } from '../api/accounts';
import './TransfersPage.css';

function TransfersPage() {
  const [transfers, setTransfers] = useState<Transfer[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formSourceId, setFormSourceId] = useState('');
  const [formTargetId, setFormTargetId] = useState('');
  const [formAmount, setFormAmount] = useState<number>(0);
  const [formDate, setFormDate] = useState<string>(new Date().toISOString().split('T')[0]);
  const [formNote, setFormNote] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      const [transfersData, accountsData] = await Promise.all([
        getTransfers(),
        getAccounts()
      ]);
      setTransfers(transfersData);
      setAccounts(accountsData);
    } catch {
      setTransfers([]);
      setAccounts([]);
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

  function getAccountName(id: string): string {
    return accounts.find(a => a.id === id)?.name || 'Desconocida';
  }

  function openCreate() {
    setEditingId(null);
    setFormSourceId('');
    setFormTargetId('');
    setFormAmount(0);
    setFormDate(new Date().toISOString().split('T')[0]);
    setFormNote('');
    setModalOpen(true);
  }

  function openEdit(transfer: Transfer) {
    setEditingId(transfer.id);
    setFormSourceId(transfer.sourceAccountId);
    setFormTargetId(transfer.targetAccountId);
    setFormAmount(transfer.amount);
    setFormDate(transfer.date);
    setFormNote(transfer.note || '');
    setModalOpen(true);
  }

  async function handleSave() {
    if (!formSourceId || !formTargetId || formSourceId === formTargetId) {
      alert('Las cuentas de origen y destino deben ser diferentes');
      return;
    }

    const data: TransferInput = {
      sourceAccountId: formSourceId,
      targetAccountId: formTargetId,
      amount: formAmount,
      date: formDate,
      note: formNote || undefined
    };

    try {
      if (editingId) {
        await updateTransfer(editingId, data);
      } else {
        await createTransfer(data);
      }
      setModalOpen(false);
      loadData();
    } catch (error) {
      alert('Error al guardar la transferencia');
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('¿Seguro que quieres eliminar esta transferencia?')) return;
    try {
      await deleteTransfer(id);
      loadData();
    } catch {
      alert('Error al eliminar');
    }
  }

  if (loading) {
    return <div className="transfers"><p>Cargando...</p></div>;
  }

  return (
    <div className="transfers">
      <div className="transfers-header">
        <h1 className="transfers-title">Transferencias</h1>
        <button className="btn-primary" onClick={openCreate}>+ Nueva transferencia</button>
      </div>

      {transfers.length > 0 ? (
        <div className="transfers-list">
          {transfers.map((transfer) => (
            <div className="transfer-card" key={transfer.id}>
              <div className="transfer-header">
                <div className="transfer-flow">
                  <div className="account-from">{transfer.sourceAccountName}</div>
                  <div className="arrow">→</div>
                  <div className="account-to">{transfer.targetAccountName}</div>
                </div>
                <div className="transfer-actions">
                  <button className="btn-icon" onClick={() => openEdit(transfer)}>Editar</button>
                  <button className="btn-icon danger" onClick={() => handleDelete(transfer.id)}>Borrar</button>
                </div>
              </div>

              <div className="transfer-details">
                <div className="detail">
                  <span className="label">Importe</span>
                  <span className="value">{formatCurrency(transfer.amount)}</span>
                </div>
                <div className="detail">
                  <span className="label">Fecha</span>
                  <span className="value">{transfer.date}</span>
                </div>
                {transfer.note && (
                  <div className="detail">
                    <span className="label">Nota</span>
                    <span className="value">{transfer.note}</span>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="transfers-empty">No tienes transferencias todavía. Crea la primera con "Nueva transferencia".</div>
      )}

      {modalOpen && (
        <div className="modal-overlay" onClick={() => setModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">{editingId ? 'Editar transferencia' : 'Nueva transferencia'}</h2>
            
            <div className="form-field">
              <label htmlFor="source">Cuenta Origen</label>
              <select
                id="source"
                value={formSourceId}
                onChange={(e) => setFormSourceId(e.target.value)}
              >
                <option value="">Selecciona cuenta</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.name}</option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="target">Cuenta Destino</label>
              <select
                id="target"
                value={formTargetId}
                onChange={(e) => setFormTargetId(e.target.value)}
              >
                <option value="">Selecciona cuenta</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.name}</option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="amount">Importe</label>
              <input
                id="amount"
                type="number"
                step="0.01"
                value={formAmount}
                onChange={(e) => setFormAmount(parseFloat(e.target.value) || 0)}
                placeholder="0.00"
              />
            </div>

            <div className="form-field">
              <label htmlFor="date">Fecha</label>
              <input
                id="date"
                type="date"
                value={formDate}
                onChange={(e) => setFormDate(e.target.value)}
              />
            </div>

            <div className="form-field">
              <label htmlFor="note">Nota (opcional)</label>
              <input
                id="note"
                type="text"
                value={formNote}
                onChange={(e) => setFormNote(e.target.value)}
                placeholder="Motivo de la transferencia"
              />
            </div>

            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setModalOpen(false)}>Cancelar</button>
              <button className="btn-primary" onClick={handleSave} disabled={!formSourceId || !formTargetId || formAmount <= 0}>
                {editingId ? 'Guardar' : 'Crear'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default TransfersPage;