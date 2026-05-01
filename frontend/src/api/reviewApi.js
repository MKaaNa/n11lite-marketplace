import apiClient from './apiClient';

export function getProductReviews(slug) {
  return apiClient.get(`/api/products/${slug}/reviews`);
}

export function createProductReview(slug, rating, comment) {
  return apiClient.post(`/api/products/${slug}/reviews`, { rating, comment });
}
