import apiClient from './apiClient';

export function initiatePayment(orderId) {
  return apiClient.post(`/api/payments/orders/${orderId}/checkout`);
}

export function getPaymentStatus(orderId) {
  return apiClient.get(`/api/payments/orders/${orderId}`);
}
