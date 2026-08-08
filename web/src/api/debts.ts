import apiClient from './client';
import type { Debt, DebtPayment, DebtType } from '../types';

export interface DebtInput {
  name: string;
  totalAmount: number;
  type: DebtType;
}

export interface DebtPaymentInput {
  amount: number;
  date: string;
}

export async function getDebts(): Promise<Debt[]> {
  const response = await apiClient.get<Debt[]>('/api/debts');
  return response.data;
}

export async function getDebt(id: string): Promise<Debt> {
  const response = await apiClient.get<Debt>(`/api/debts/${id}`);
  return response.data;
}

export async function createDebt(data: DebtInput): Promise<Debt> {
  const response = await apiClient.post<Debt>('/api/debts', data);
  return response.data;
}

export async function updateDebt(id: string, data: DebtInput): Promise<Debt> {
  const response = await apiClient.put<Debt>(`/api/debts/${id}`, data);
  return response.data;
}

export async function deleteDebt(id: string): Promise<void> {
  await apiClient.delete(`/api/debts/${id}`);
}

export async function addDebtPayment(debtId: string, data: DebtPaymentInput): Promise<DebtPayment> {
  const response = await apiClient.post<DebtPayment>(`/api/debts/${debtId}/payments`, data);
  return response.data;
}

export async function getDebtPayments(debtId: string): Promise<DebtPayment[]> {
  const response = await apiClient.get<DebtPayment[]>(`/api/debts/${debtId}/payments`);
  return response.data;
}

export async function deleteDebtPayment(debtId: string, paymentId: string): Promise<void> {
  await apiClient.delete(`/api/debts/${debtId}/payments/${paymentId}`);
}