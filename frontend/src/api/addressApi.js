import apiClient from './apiClient';

export function listAddresses() {
  return apiClient.get('/api/me/addresses');
}

export function createAddress(body) {
  return apiClient.post('/api/me/addresses', body);
}

export function updateAddress(addressId, body) {
  return apiClient.put(`/api/me/addresses/${addressId}`, body);
}

export function deleteAddress(addressId) {
  return apiClient.delete(`/api/me/addresses/${addressId}`);
}

export function setDefaultAddress(addressId) {
  return apiClient.put(`/api/me/addresses/${addressId}/default`);
}
