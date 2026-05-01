import apiClient from './apiClient';

export function register(request) {
  return apiClient.post('/api/auth/register', request);
}

export function login(request) {
  return apiClient.post('/api/auth/login', request);
}

export function verifyLogin(request) {
  return apiClient.post('/api/auth/verify-login', request);
}

export function getCurrentUser() {
  return apiClient.get('/api/auth/me');
}
