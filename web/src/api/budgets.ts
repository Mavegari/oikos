import apiClient from './client';
import type { Budget } from '../types';

export interface BudgetInput {
  categoryId: string;
  limitAmount: number;
  month: number;
  year: number;
}

export async function getBudgets(month?: number, year?: number): Promise<Budget[]> {
  const params = month && year ? { month, year } : {};
  const response = await apiClient.get<Budget[]>('/api/budgets', { params });
  return response.data;
}

export async function createBudget(data: BudgetInput): Promise<Budget> {
  const response = await apiClient.post<Budget>('/api/budgets', data);
  return response.data;
}

export async function updateBudget(id: string, data: BudgetInput): Promise<Budget> {
  const response = await apiClient.put<Budget>(`/api/budgets/${id}`, data);
  return response.data;
}

export async function deleteBudget(id: string): Promise<void> {
  await apiClient.delete(`/api/budgets/${id}`);
}