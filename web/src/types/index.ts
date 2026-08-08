// ============================================
// OIKOS · Tipos (equivalen a los DTOs del backend)
// ============================================

// --- Autenticación ---

// Lo que enviamos al hacer login (= LoginRequest.java)
export interface LoginRequest {
  email: string;
  password: string;
}

// Lo que enviamos al registrarnos (= RegisterRequest.java)
export interface RegisterRequest {
  email: string;
  password: string;
}

// Lo que devuelve el login (= AuthResponse.java)
export interface AuthResponse {
  token: string;
  type: string;
}

// Lo que devuelve el registro (= UserResponse.java)
export interface UserResponse {
  id: string;
  email: string;
  createdAt: string;
}


// --- Cuentas (= AccountResponse.java) ---
export type AccountType = 'CASH' | 'BANK' | 'CARD';
export interface Account {
  id: string;
  name: string;
  type: AccountType;
  initialBalance: number;
  balance: number;
}

// --- Categorías (= CategoryResponse.java) ---

export type CategoryType = 'INCOME' | 'EXPENSE';

export interface Category {
  id: string;
  name: string;
  type: CategoryType;
  color: string | null;
}

// --- Transacciones (= TransactionResponse.java) ---

export interface Transaction {
  id: string;
  accountId: string;
  accountName: string;
  categoryId: string;
  categoryName: string;
  amount: number;
  type: CategoryType;
  date: string;
  note: string | null;
}

// --- Presupuestos (= BudgetResponse.java) ---

export interface Budget {
  id: string;
  categoryId: string;
  categoryName: string;
  limitAmount: number;
  spent: number;
  remaining: number;
  month: number;
  year: number;
  exceeded: boolean;
}

// --- Transferencias (= TransferResponse.java) ---
export interface Transfer {
  id: string;
  sourceAccountId: string;
  sourceAccountName: string;
  targetAccountId: string;
  targetAccountName: string;
  amount: number;
  date: string;
  note: string | null;
}

// --- Presupuestos Recurrentes (= BudgetResponse.java actualizado) ---
export interface BudgetRecurring extends Budget {
  recurring: boolean;
}

// --- Inversiones (= InvestmentResponse.java) ---
export interface Investment {
  id: string;
  name: string;
  investedAmount: number;
  currentValue: number;
  profitLoss: number;
  profitLossPercentage: number;
  valuations: InvestmentValuation[];
  createdAt: string;
}

export interface InvestmentValuation {
  id: string;
  value: number;
  date: string;
}

// --- Deudas (= DebtResponse.java) ---
export type DebtType = 'I_OWE' | 'OWED_TO_ME';

export interface Debt {
  id: string;
  name: string;
  totalAmount: number;
  paid: number;
  pending: number;
  pendingPercentage: number;
  type: DebtType;
  payments: DebtPayment[];
  createdAt: string;
}

export interface DebtPayment {
  id: string;
  amount: number;
  date: string;
}