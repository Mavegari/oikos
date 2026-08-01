import apiClient from './client';
import type { LoginRequest, RegisterRequest, AuthResponse, UserResponse } from '../types';

// Iniciar sesión: envía email+password, recibe el token
export async function login(data: LoginRequest): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>('/api/auth/login', data);
  return response.data;
}

// Registrarse: envía email+password, recibe el usuario creado
export async function register(data: RegisterRequest): Promise<UserResponse> {
  const response = await apiClient.post<UserResponse>('/api/auth/register', data);
  return response.data;
}