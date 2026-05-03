import apiClient from './apiClient';

export function createOrder({ shippingAddress, couponCode }) {
  return apiClient.post('/api/orders', {
    shippingAddress,
    couponCode: couponCode || null,
  });
}
