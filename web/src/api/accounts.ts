import apiClient from './client';
import type { Account, AccountType } from '../types';

// Datos para crear/editar una cuenta
export interface AccountInput {
  name: string;
  type: AccountType;
  initialBalance: number;
}

// Listar todas las cuentas del usuario
export async function getAccounts(): Promise<Account[]> {
  const response = await apiClient.get<Account[]>('/api/accounts');
  return response.data;
}

// Crear una cuenta
export async function createAccount(data: AccountInput): Promise<Account> {
  const response = await apiClient.post<Account>('/api/accounts', data);
  return response.data;
}

// Editar una cuenta
export async function updateAccount(id: string, data: AccountInput): Promise<Account> {
  const response = await apiClient.put<Account>(`/api/accounts/${id}`, data);
  return response.data;
}

// Eliminar una cuenta
export async function deleteAccount(id: string): Promise<void> {
  await apiClient.delete(`/api/accounts/${id}`);
}