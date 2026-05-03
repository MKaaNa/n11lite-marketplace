import apiClient from './apiClient';

export function createOrder({ shippingAddress, savedAddressId, couponCode }) {
  return apiClient.post('/api/orders', {
    shippingAddress: shippingAddress || null,
    savedAddressId: savedAddressId ?? null,
    couponCode: couponCode || null,
  });
}
