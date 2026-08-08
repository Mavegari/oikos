import apiClient from './client';
import type { Transfer } from '../types';

export interface TransferInput {
  sourceAccountId: string;
  targetAccountId: string;
  amount: number;
  date: string;
  note?: string;
}

export async function getTransfers(): Promise<Transfer[]> {
  const response = await apiClient.get<Transfer[]>('/api/transfers');
  return response.data;
}

export async function createTransfer(data: TransferInput): Promise<Transfer> {
  const response = await apiClient.post<Transfer>('/api/transfers', data);
  return response.data;
}

export async function updateTransfer(id: string, data: TransferInput): Promise<Transfer> {
  const response = await apiClient.put<Transfer>(`/api/transfers/${id}`, data);
  return response.data;
}

export async function deleteTransfer(id: string): Promise<void> {
  await apiClient.delete(`/api/transfers/${id}`);
}