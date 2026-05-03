import apiClient from './apiClient';

const SESSION_KEY = 'n11lite_session_id';

export function getOrCreateSessionId() {
  let sessionId = localStorage.getItem(SESSION_KEY);

  if (!sessionId) {
    sessionId = crypto.randomUUID();
    localStorage.setItem(SESSION_KEY, sessionId);
  }

  return sessionId;
}

export function trackProductView(slug, sessionId) {
  return apiClient.post(`/api/recommendations/views/${slug}`, null, {
    headers: { 'X-Session-Id': sessionId },
  });
}

export function getRecommendations(sessionId, limit = 4, currentSlug = null) {
  const params = { limit };
  if (currentSlug) {
    params.currentSlug = currentSlug;
  }

  return apiClient.get('/api/recommendations', {
    params,
    headers: sessionId ? { 'X-Session-Id': sessionId } : {},
  });
}
