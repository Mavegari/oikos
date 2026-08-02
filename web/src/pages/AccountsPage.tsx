import { useState, useEffect } from 'react';
import { getAccounts, createAccount, updateAccount, deleteAccount } from '../api/accounts.ts';
import type { AccountInput } from '../api/accounts.ts';
import type { Account, AccountType } from '../types';
import './AccountsPage.css';

const TYPE_LABELS: Record<AccountType, string> = {
  CASH: 'Efectivo',
  BANK: 'Banco',
  CARD: 'Tarjeta',
};

function AccountsPage() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);

  // Estado del modal
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formName, setFormName] = useState('');
  const [formType, setFormType] = useState<AccountType>('BANK');

  // Cargar cuentas al montar
  useEffect(() => {
    loadAccounts();
  }, []);

  function loadAccounts() {
    getAccounts()
      .then((data) => setAccounts(data))
      .catch(() => setAccounts([]))
      .finally(() => setLoading(false));
  }

  function formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
    }).format(amount);
  }

  // Abrir modal para crear
  function openCreate() {
    setEditingId(null);
    setFormName('');
    setFormType('BANK');
    setModalOpen(true);
  }

  // Abrir modal para editar
  function openEdit(account: Account) {
    setEditingId(account.id);
    setFormName(account.name);
    setFormType(account.type);
    setModalOpen(true);
  }

  // Guardar (crear o editar según el caso)
  async function handleSave() {
    const data: AccountInput = { name: formName, type: formType };
    if (editingId) {
      await updateAccount(editingId, data);
    } else {
      await createAccount(data);
    }
    setModalOpen(false);
    loadAccounts();
  }

  // Eliminar
  async function handleDelete(id: string) {
    if (!confirm('¿Seguro que quieres eliminar esta cuenta?')) return;
    await deleteAccount(id);
    loadAccounts();
  }

  if (loading) {
    return <div className="accounts"><p>Cargando...</p></div>;
  }

  return (
    <div className="accounts">
      <div className="accounts-header">
        <h1 className="accounts-title">Cuentas</h1>
        <button className="btn-primary" onClick={openCreate}>
          + Nueva cuenta
        </button>
      </div>

      {accounts.length > 0 ? (
        <div className="accounts-list">
          {accounts.map((account) => (
            <div className="account-card" key={account.id}>
              <div className="account-info">
                <div className="account-icon">{TYPE_LABELS[account.type].slice(0, 3).toUpperCase()}</div>
                <div>
                  <div className="account-name">{account.name}</div>
                  <div className="account-type">{TYPE_LABELS[account.type]}</div>
                </div>
              </div>
              <div className="account-right">
                <span className="account-balance">{formatCurrency(account.balance)}</span>
                <div className="account-actions">
                  <button className="btn-icon" onClick={() => openEdit(account)}>Editar</button>
                  <button className="btn-icon danger" onClick={() => handleDelete(account.id)}>Borrar</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="accounts-empty">
          No tienes cuentas todavía. Crea la primera con "Nueva cuenta".
        </div>
      )}

      {modalOpen && (
        <div className="modal-overlay" onClick={() => setModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">
              {editingId ? 'Editar cuenta' : 'Nueva cuenta'}
            </h2>

            <div className="form-field">
              <label htmlFor="name">Nombre</label>
              <input
                id="name"
                type="text"
                value={formName}
                onChange={(e) => setFormName(e.target.value)}
                placeholder="Cuenta corriente"
              />
            </div>

            <div className="form-field">
              <label htmlFor="type">Tipo</label>
              <select
                id="type"
                value={formType}
                onChange={(e) => setFormType(e.target.value as AccountType)}
              >
                <option value="BANK">Banco</option>
                <option value="CASH">Efectivo</option>
                <option value="CARD">Tarjeta</option>
              </select>
            </div>

            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setModalOpen(false)}>
                Cancelar
              </button>
              <button className="btn-primary" onClick={handleSave} disabled={!formName}>
                {editingId ? 'Guardar' : 'Crear'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AccountsPage;