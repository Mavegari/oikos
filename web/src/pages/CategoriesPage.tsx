import { useState, useEffect } from 'react';
import { getCategories, createCategory, updateCategory, deleteCategory } from '../api/categories.ts';
import type { CategoryInput } from '../api/categories.ts';
import type { Category, CategoryType } from '../types';
import './CategoriesPage.css';

const DEFAULT_COLOR = '#1D9E75';

function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formName, setFormName] = useState('');
  const [formType, setFormType] = useState<CategoryType>('EXPENSE');
  const [formColor, setFormColor] = useState(DEFAULT_COLOR);

  useEffect(() => {
    loadCategories();
  }, []);

  function loadCategories() {
    getCategories()
      .then((data) => setCategories(data))
      .catch(() => setCategories([]))
      .finally(() => setLoading(false));
  }

  function openCreate() {
    setEditingId(null);
    setFormName('');
    setFormType('EXPENSE');
    setFormColor(DEFAULT_COLOR);
    setModalOpen(true);
  }

  function openEdit(category: Category) {
    setEditingId(category.id);
    setFormName(category.name);
    setFormType(category.type);
    setFormColor(category.color || DEFAULT_COLOR);
    setModalOpen(true);
  }

  async function handleSave() {
    const data: CategoryInput = { name: formName, type: formType, color: formColor };
    if (editingId) {
      await updateCategory(editingId, data);
    } else {
      await createCategory(data);
    }
    setModalOpen(false);
    loadCategories();
  }

  async function handleDelete(id: string) {
    if (!confirm('¿Seguro que quieres eliminar esta categoría?')) return;
    await deleteCategory(id);
    loadCategories();
  }

  const incomeCategories = categories.filter((c) => c.type === 'INCOME');
  const expenseCategories = categories.filter((c) => c.type === 'EXPENSE');

  if (loading) {
    return <div className="categories"><p>Cargando...</p></div>;
  }

  function renderList(list: Category[]) {
    return (
      <div className="categories-list">
        {list.map((cat) => (
          <div className="category-item" key={cat.id}>
            <div className="category-left">
              <span className="category-dot" style={{ background: cat.color || DEFAULT_COLOR }}></span>
              <span className="category-name">{cat.name}</span>
            </div>
            <div className="category-actions">
              <button className="btn-icon" onClick={() => openEdit(cat)}>Editar</button>
              <button className="btn-icon danger" onClick={() => handleDelete(cat.id)}>Borrar</button>
            </div>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="categories">
      <div className="categories-header">
        <h1 className="categories-title">Categorías</h1>
        <button className="btn-primary" onClick={openCreate}>+ Nueva categoría</button>
      </div>

      {categories.length === 0 ? (
        <div className="accounts-empty">
          No tienes categorías todavía. Crea la primera con "Nueva categoría".
        </div>
      ) : (
        <>
          <div className="categories-section">
            <div className="categories-section-title">Ingresos</div>
            {incomeCategories.length > 0 ? renderList(incomeCategories) : <p style={{ color: 'var(--oikos-text-muted)', fontSize: '0.9rem' }}>Sin categorías de ingreso</p>}
          </div>
          <div className="categories-section">
            <div className="categories-section-title">Gastos</div>
            {expenseCategories.length > 0 ? renderList(expenseCategories) : <p style={{ color: 'var(--oikos-text-muted)', fontSize: '0.9rem' }}>Sin categorías de gasto</p>}
          </div>
        </>
      )}

      {modalOpen && (
        <div className="modal-overlay" onClick={() => setModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">{editingId ? 'Editar categoría' : 'Nueva categoría'}</h2>

            <div className="form-field">
              <label htmlFor="name">Nombre</label>
              <input id="name" type="text" value={formName} onChange={(e) => setFormName(e.target.value)} placeholder="Alimentación" />
            </div>

            <div className="form-field">
              <label htmlFor="type">Tipo</label>
              <select id="type" value={formType} onChange={(e) => setFormType(e.target.value as CategoryType)}>
                <option value="EXPENSE">Gasto</option>
                <option value="INCOME">Ingreso</option>
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="color">Color</label>
              <input id="color" type="color" value={formColor} onChange={(e) => setFormColor(e.target.value)} style={{ height: '40px', padding: '4px' }} />
            </div>

            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setModalOpen(false)}>Cancelar</button>
              <button className="btn-primary" onClick={handleSave} disabled={!formName}>{editingId ? 'Guardar' : 'Crear'}</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default CategoriesPage;