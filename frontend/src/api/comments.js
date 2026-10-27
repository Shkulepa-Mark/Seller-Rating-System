import { http } from './client';
import { ENDPOINTS } from './endpoints';

export const commentsApi = {
  list: (sellerId, signal) => http.get(ENDPOINTS.comments.collection(sellerId), { signal }),
  create: (sellerId, payload) => http.post(ENDPOINTS.comments.collection(sellerId), payload),
  update: (sellerId, commentId, payload) => http.put(ENDPOINTS.comments.byId(sellerId, commentId), payload),
  remove: (sellerId, commentId) => http.delete(ENDPOINTS.comments.byId(sellerId, commentId)),
};
