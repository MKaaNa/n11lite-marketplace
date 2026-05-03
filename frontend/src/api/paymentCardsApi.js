import apiClient from './apiClient';

export function listPaymentCards() {
  return apiClient.get('/api/me/payment-cards');
}

export function registerPaymentCard(body) {
  return apiClient.post('/api/me/payment-cards', body);
}

export function deletePaymentCard(cardToken) {
  return apiClient.delete(`/api/me/payment-cards/${encodeURIComponent(cardToken)}`);
}
