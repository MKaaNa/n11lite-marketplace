import apiClient from './apiClient';

export function getAdminOrders() {
  return apiClient.get('/api/admin/orders');
}

export function getAdminOrder(orderId) {
  return apiClient.get(`/api/admin/orders/${orderId}`);
}

export function updateOrderStatus(orderId, status) {
  return apiClient.put(`/api/admin/orders/${orderId}/status`, { status });
}
