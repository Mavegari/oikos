import apiClient from './client';
import type { Category, CategoryType } from '../types';

export interface CategoryInput {
  name: string;
  type: CategoryType;
  color: string | null;
}

export async function getCategories(): Promise<Category[]> {
  const response = await apiClient.get<Category[]>('/api/categories');
  return response.data;
}

export async function createCategory(data: CategoryInput): Promise<Category> {
  const response = await apiClient.post<Category>('/api/categories', data);
  return response.data;
}

export async function updateCategory(id: string, data: CategoryInput): Promise<Category> {
  const response = await apiClient.put<Category>(`/api/categories/${id}`, data);
  return response.data;
}

export async function deleteCategory(id: string): Promise<void> {
  await apiClient.delete(`/api/categories/${id}`);
}