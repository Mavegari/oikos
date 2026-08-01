import axios from 'axios';

// El cliente central: sabe la URL base de la API
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor: añade el token JWT a cada petición, si existe
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('oikos_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;