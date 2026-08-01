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