import apiClient from './apiClient';

export function getCategories() {
  return apiClient.get('/api/categories');
}

export function getProducts({ category, search, page = 0, size = 12, sort = 'recommended' }) {
  return apiClient.get('/api/products', {
    params: {
      category: category || undefined,
      search: search || undefined,
      page,
      size,
      sort,
    },
  });
}

export function getProductBySlug(slug) {
  return apiClient.get(`/api/products/${slug}`);
}
