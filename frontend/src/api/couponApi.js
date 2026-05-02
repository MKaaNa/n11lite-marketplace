import apiClient from './apiClient';

export function validateCoupon(code, cartTotal) {
  return apiClient.post('/api/coupons/validate', {
    code,
    cartTotal,
  });
}
