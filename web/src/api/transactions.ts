import apiClient from './client';
import type { Transaction, CategoryType } from '../types';

// Datos para crear/editar una transacción
export interface TransactionInput {
  accountId: string;
  categoryId: string;
  amount: number;
  type: CategoryType;
  date: string;      // formato "2026-08-01"
  note: string | null;
}

export async function getTransactions(): Promise<Transaction[]> {
  const response = await apiClient.get<Transaction[]>('/api/transactions');
  return response.data;
}

export async function createTransaction(data: TransactionInput): Promise<Transaction> {
  const response = await apiClient.post<Transaction>('/api/transactions', data);
  return response.data;
}

export async function updateTransaction(id: string, data: TransactionInput): Promise<Transaction> {
  const response = await apiClient.put<Transaction>(`/api/transactions/${id}`, data);
  return response.data;
}

export async function deleteTransaction(id: string): Promise<void> {
  await apiClient.delete(`/api/transactions/${id}`);
}