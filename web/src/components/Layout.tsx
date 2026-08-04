import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import type { ReactNode } from 'react';
import { useAuth } from '../context/AuthContext.tsx';
import { useTheme } from '../context/ThemeContext.tsx';
import './Layout.css';

const NAV_ITEMS = [
  { to: '/', label: 'Dashboard', icon: '▨', end: true },
  { to: '/accounts', label: 'Cuentas', icon: '▤', end: false },
  { to: '/categories', label: 'Categorías', icon: '◈', end: false },
  { to: '/transactions', label: 'Transacciones', icon: '⇄', end: false },
  { to: '/budgets', label: 'Presupuestos', icon: '◎', end: false },
];

function Layout({ children }: { children: ReactNode }) {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const { theme, toggleTheme } = useTheme();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <div className="layout">
      <aside className={collapsed ? 'sidebar collapsed' : 'sidebar'}>
        <div className="sidebar-logo">
          <div
            className="sidebar-logo-mark"
            onClick={() => collapsed && setCollapsed(false)}
            style={{ cursor: collapsed ? 'pointer' : 'default' }}
          >
            O
          </div>
          {!collapsed && <span>Oikos</span>}
          {!collapsed && (
            <button
              className="sidebar-toggle"
              onClick={() => setCollapsed(true)}
              title="Colapsar"
            >
              «
            </button>
          )}
        </div>

        <nav className="sidebar-nav">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              title={item.label}
              className={({ isActive }) =>
                isActive ? 'sidebar-link active' : 'sidebar-link'
              }
            >
              <span className="sidebar-icon">{item.icon}</span>
              {!collapsed && <span>{item.label}</span>}
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <button className="sidebar-theme-toggle" onClick={toggleTheme} title="Cambiar tema">
            <span className="sidebar-icon">{theme === 'light' ? '☾' : '☀'}</span>
            {!collapsed && <span>{theme === 'light' ? 'Modo oscuro' : 'Modo claro'}</span>}
          </button>
          <div className="sidebar-user">
            <div className="sidebar-avatar">OK</div>
            {!collapsed && <span className="sidebar-email">Mi cuenta</span>}
            <button className="sidebar-logout" onClick={handleLogout} title="Cerrar sesión">
              ⎋
            </button>
          </div>
        </div>
      </aside>

      <main className="layout-content">
        {children}
      </main>
    </div>
  );
}

export default Layout;