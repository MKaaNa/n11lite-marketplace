import axios from 'axios';

const TOKEN_KEY = 'n11lite_token';

const envBase = import.meta.env.VITE_API_BASE_URL;
const baseURL =
  typeof envBase === 'string' && envBase.trim() !== ''
    ? envBase.trim()
    : import.meta.env.PROD
      ? ''
      : 'http://localhost:8080';

const apiClient = axios.create({
  baseURL,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function saveToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function removeStoredToken() {
  localStorage.removeItem(TOKEN_KEY);
}

export default apiClient;
