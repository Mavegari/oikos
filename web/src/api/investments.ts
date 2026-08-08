import apiClient from './client';
import type { Investment, InvestmentValuation } from '../types';

export interface InvestmentInput {
  name: string;
  investedAmount: number;
}

export interface InvestmentValuationInput {
  value: number;
  date: string;
}

export async function getInvestments(): Promise<Investment[]> {
  const response = await apiClient.get<Investment[]>('/api/investments');
  return response.data;
}

export async function getInvestment(id: string): Promise<Investment> {
  const response = await apiClient.get<Investment>(`/api/investments/${id}`);
  return response.data;
}

export async function createInvestment(data: InvestmentInput): Promise<Investment> {
  const response = await apiClient.post<Investment>('/api/investments', data);
  return response.data;
}

export async function updateInvestment(id: string, data: InvestmentInput): Promise<Investment> {
  const response = await apiClient.put<Investment>(`/api/investments/${id}`, data);
  return response.data;
}

export async function deleteInvestment(id: string): Promise<void> {
  await apiClient.delete(`/api/investments/${id}`);
}

export async function addInvestmentValuation(investmentId: string, data: InvestmentValuationInput): Promise<InvestmentValuation> {
  const response = await apiClient.post<InvestmentValuation>(`/api/investments/${investmentId}/valuations`, data);
  return response.data;
}

export async function getInvestmentValuations(investmentId: string): Promise<InvestmentValuation[]> {
  const response = await apiClient.get<InvestmentValuation[]>(`/api/investments/${investmentId}/valuations`);
  return response.data;
}

export async function deleteInvestmentValuation(investmentId: string, valuationId: string): Promise<void> {
  await apiClient.delete(`/api/investments/${investmentId}/valuations/${valuationId}`);
}