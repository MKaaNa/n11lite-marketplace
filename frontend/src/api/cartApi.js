import apiClient from './apiClient';

export function getCart() {
  return apiClient.get('/api/cart');
}

export function addToCart(productId, quantity) {
  return apiClient.post('/api/cart/items', {
    productId,
    quantity,
  });
}

export function updateCartItem(itemId, quantity) {
  return apiClient.put(`/api/cart/items/${itemId}`, {
    quantity,
  });
}

export function removeCartItem(itemId) {
  return apiClient.delete(`/api/cart/items/${itemId}`);
}

export function clearCart() {
  return apiClient.delete('/api/cart/items');
}
