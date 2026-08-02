import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.tsx';
import { getSummary } from '../api/dashboard.ts';
import type { DashboardSummary } from '../api/dashboard.ts';
import './DashboardPage.css';

function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);

  const { logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    getSummary()
      .then((data) => setSummary(data))
      .catch(() => setSummary(null))
      .finally(() => setLoading(false));
  }, []);

  function handleLogout() {
    logout();
    navigate('/login');
  }

  // Formatea números como moneda española
  function formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
    }).format(amount);
  }

  if (loading) {
    return <div className="dashboard-loading">Cargando...</div>;
  }

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <div className="dashboard-logo">
          <div className="dashboard-logo-mark">O</div>
          <span>Oikos</span>
        </div>
        <button className="dashboard-logout" onClick={handleLogout}>
          Cerrar sesión
        </button>
      </div>

      {summary ? (
        <>
          <div className="metrics-grid">
            <div className="metric-card">
              <div className="metric-label">Balance total</div>
              <div className="metric-value">{formatCurrency(summary.totalBalance)}</div>
            </div>
            <div className="metric-card">
              <div className="metric-label">Ingresos del mes</div>
              <div className="metric-value income">{formatCurrency(summary.monthlyIncome)}</div>
            </div>
            <div className="metric-card">
              <div className="metric-label">Gastos del mes</div>
              <div className="metric-value expense">{formatCurrency(summary.monthlyExpenses)}</div>
            </div>
          </div>

          <h2 className="section-title">Gasto por categoría</h2>
          {summary.spendingByCategory.length > 0 ? (
            <div className="category-list">
              {summary.spendingByCategory.map((cat) => (
                <div className="category-row" key={cat.categoryName}>
                  <span>{cat.categoryName}</span>
                  <span className="category-amount">{formatCurrency(cat.total)}</span>
                </div>
              ))}
            </div>
          ) : (
            <div className="dashboard-empty">No hay gastos este mes</div>
          )}
        </>
      ) : (
        <div className="dashboard-empty">No se pudieron cargar los datos</div>
      )}
    </div>
  );
}

export default DashboardPage;