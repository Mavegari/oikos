import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { login as loginApi } from '../api/auth';
import type { LoginRequest } from '../types';

// La forma del contexto: qué ofrece a la app
interface AuthContextType {
  token: string | null;
  isAuthenticated: boolean;
  login: (data: LoginRequest) => Promise<void>;
  logout: () => void;
}

// Creamos el contexto (vacío al principio)
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// El "proveedor": envuelve la app y le da acceso al contexto
export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem('oikos_token')
  );

  // Función de login: llama a la API, guarda el token
  async function login(data: LoginRequest) {
    const response = await loginApi(data);
    localStorage.setItem('oikos_token', response.token);
    setToken(response.token);
  }

  // Función de logout: borra el token
  function logout() {
    localStorage.removeItem('oikos_token');
    setToken(null);
  }

  const value: AuthContextType = {
    token,
    isAuthenticated: token !== null,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// Hook para usar el contexto fácilmente desde cualquier componente
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider');
  }
  return context;
}