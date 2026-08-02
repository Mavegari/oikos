import apiClient from './client';

export interface CategorySpending {
  categoryName: string;
  total: number;
}

export interface DashboardSummary {
  totalBalance: number;
  monthlyIncome: number;
  monthlyExpenses: number;
  monthlyBalance: number;
  month: number;
  year: number;
  spendingByCategory: CategorySpending[];
}

// Trae el resumen del dashboard (mes actual si no se indica)
export async function getSummary(month?: number, year?: number): Promise<DashboardSummary> {
  const params = month && year ? { month, year } : {};
  const response = await apiClient.get<DashboardSummary>('/api/dashboard/summary', { params });
  return response.data;
}