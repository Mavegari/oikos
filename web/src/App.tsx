import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage.tsx';
import DashboardPage from './pages/DashboardPage.tsx';
import ProtectedRoute from './components/ProtectedRoute.tsx';
import AccountsPage from './pages/AccountsPage.tsx';
import CategoriesPage from './pages/CategoriesPage.tsx';
import TransactionsPage from './pages/TransactionsPage.tsx';
import Layout from './components/Layout.tsx';
import BudgetsPage from './pages/BudgetsPage.tsx';
import RegisterPage from './pages/RegisterPage.tsx';
import InvestmentsPage from './pages/InvestmentsPage.tsx';
import DebtsPage from './pages/DebtsPage.tsx';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rutas públicas */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Rutas protegidas */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Layout>
                <DashboardPage />
              </Layout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/accounts"
          element={
            <ProtectedRoute>
               <Layout>
              <AccountsPage />
              </Layout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/categories"
          element={
            <ProtectedRoute>
              <Layout>
              <CategoriesPage />
              </Layout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/transactions"
          element={
            <ProtectedRoute>
              <Layout>
              <TransactionsPage />
              </Layout>
            </ProtectedRoute>
          }
        />
        
        <Route
          path="/budgets"
          element={
            <ProtectedRoute>
              <Layout>
                <BudgetsPage />
              </Layout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/investments"
          element={
            <ProtectedRoute>
              <Layout>
                <InvestmentsPage />
              </Layout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/debts"
          element={
            <ProtectedRoute>
              <Layout>
                <DebtsPage />
              </Layout>
            </ProtectedRoute>
          }
        />

        {/* Cualquier otra ruta → login */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;