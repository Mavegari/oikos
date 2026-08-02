import { useState, useEffect } from 'react';
import { getTransactions, createTransaction, updateTransaction, deleteTransaction } from '../api/transactions.ts';
import type { TransactionInput } from '../api/transactions.ts';
import { getAccounts } from '../api/accounts.ts';
import { getCategories } from '../api/categories.ts';
import type { Transaction, Account, Category, CategoryType } from '../types';
import './TransactionsPage.css';

function todayISO(): string {
  return new Date().toISOString().slice(0, 10);
}

function TransactionsPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formType, setFormType] = useState<CategoryType>('EXPENSE');
  const [formAccountId, setFormAccountId] = useState('');
  const [formCategoryId, setFormCategoryId] = useState('');
  const [formAmount, setFormAmount] = useState('');
  const [formDate, setFormDate] = useState(todayISO());
  const [formNote, setFormNote] = useState('');

  useEffect(() => {
    loadAll();
  }, []);

  async function loadAll() {
    try {
      const [txs, accs, cats] = await Promise.all([
        getTransactions(),
        getAccounts(),
        getCategories(),
      ]);
      setTransactions(txs);
      setAccounts(accs);
      setCategories(cats);
    } catch {
      // dejamos las listas vacías si algo falla
    } finally {
      setLoading(false);
    }
  }

  function loadTransactions() {
    getTransactions().then(setTransactions).catch(() => {});
  }

  function formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(amount);
  }

  function formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  function openCreate() {
    setEditingId(null);
    setFormType('EXPENSE');
    setFormAccountId(accounts[0]?.id || '');
    setFormCategoryId('');
    setFormAmount('');
    setFormDate(todayISO());
    setFormNote('');
    setModalOpen(true);
  }

  function openEdit(tx: Transaction) {
    setEditingId(tx.id);
    setFormType(tx.type);
    setFormAccountId(tx.accountId);
    setFormCategoryId(tx.categoryId);
    setFormAmount(String(tx.amount));
    setFormDate(tx.date);
    setFormNote(tx.note || '');
    setModalOpen(true);
  }

  async function handleSave() {
    const data: TransactionInput = {
      accountId: formAccountId,
      categoryId: formCategoryId,
      amount: parseFloat(formAmount),
      type: formType,
      date: formDate,
      note: formNote || null,
    };
    if (editingId) {
      await updateTransaction(editingId, data);
    } else {
      await createTransaction(data);
    }
    setModalOpen(false);
    loadTransactions();
  }

  async function handleDelete(id: string) {
    if (!confirm('¿Seguro que quieres eliminar esta transacción?')) return;
    await deleteTransaction(id);
    loadTransactions();
  }

  // Categorías filtradas según el tipo elegido en el formulario
  const filteredCategories = categories.filter((c) => c.type === formType);

  if (loading) {
    return <div className="transactions"><p>Cargando...</p></div>;
  }

  const canSave = formAccountId && formCategoryId && formAmount && parseFloat(formAmount) > 0;

  return (
    <div className="transactions">
      <div className="transactions-header">
        <h1 className="transactions-title">Transacciones</h1>
        <button className="btn-primary" onClick={openCreate} disabled={accounts.length === 0 || categories.length === 0}>
          + Nueva transacción
        </button>
      </div>

      {accounts.length === 0 || categories.length === 0 ? (
        <div className="accounts-empty">
          Necesitas al menos una cuenta y una categoría para crear transacciones.
        </div>
      ) : transactions.length > 0 ? (
        <div className="transactions-list">
          {transactions.map((tx) => (
            <div className="transaction-item" key={tx.id}>
              <div className="transaction-left">
                <div className={`transaction-icon ${tx.type === 'INCOME' ? 'income' : 'expense'}`}>
                  {tx.type === 'INCOME' ? '↑' : '↓'}
                </div>
                <div className="transaction-details">
                  <span className="transaction-category">{tx.categoryName}</span>
                  <span className="transaction-meta">{tx.accountName} · {formatDate(tx.date)}</span>
                </div>
              </div>
              <div className="transaction-right">
                <span className={`transaction-amount ${tx.type === 'INCOME' ? 'income' : 'expense'}`}>
                  {tx.type === 'INCOME' ? '+' : '−'}{formatCurrency(tx.amount)}
                </span>
                <div className="transaction-actions">
                  <button className="btn-icon" onClick={() => openEdit(tx)}>Editar</button>
                  <button className="btn-icon danger" onClick={() => handleDelete(tx.id)}>Borrar</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="accounts-empty">No hay transacciones todavía.</div>
      )}

      {modalOpen && (
        <div className="modal-overlay" onClick={() => setModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">{editingId ? 'Editar transacción' : 'Nueva transacción'}</h2>

            {/* Selector de tipo (ingreso/gasto) */}
            <div className="type-toggle">
              <button
                className={formType === 'EXPENSE' ? 'active-expense' : ''}
                onClick={() => { setFormType('EXPENSE'); setFormCategoryId(''); }}
              >
                Gasto
              </button>
              <button
                className={formType === 'INCOME' ? 'active-income' : ''}
                onClick={() => { setFormType('INCOME'); setFormCategoryId(''); }}
              >
                Ingreso
              </button>
            </div>

            <div className="form-field">
              <label htmlFor="amount">Importe (€)</label>
              <input id="amount" type="number" step="0.01" min="0" value={formAmount}
                onChange={(e) => setFormAmount(e.target.value)} placeholder="0,00" />
            </div>

            <div className="form-field">
              <label htmlFor="account">Cuenta</label>
              <select id="account" value={formAccountId} onChange={(e) => setFormAccountId(e.target.value)}>
                {accounts.map((acc) => (
                  <option key={acc.id} value={acc.id}>{acc.name}</option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="category">Categoría</label>
              <select id="category" value={formCategoryId} onChange={(e) => setFormCategoryId(e.target.value)}>
                <option value="">Selecciona una categoría</option>
                {filteredCategories.map((cat) => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="date">Fecha</label>
              <input id="date" type="date" value={formDate} onChange={(e) => setFormDate(e.target.value)} />
            </div>

            <div className="form-field">
              <label htmlFor="note">Nota (opcional)</label>
              <input id="note" type="text" value={formNote} onChange={(e) => setFormNote(e.target.value)} placeholder="Compra semanal" />
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

export default TransactionsPage;