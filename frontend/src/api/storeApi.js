import apiClient from './apiClient';

export function getStoreReviews(storeId) {
  return apiClient.get(`/api/stores/${storeId}/reviews`);
}
