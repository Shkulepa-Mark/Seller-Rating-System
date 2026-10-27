import { http } from './client';
import { ENDPOINTS } from './endpoints';

export const sellersApi = {
  getAll: (query, signal) => http.get(ENDPOINTS.sellers.collection, { signal, params: query }),
  getById: (id, signal) => http.get(ENDPOINTS.sellers.byId(id), { signal }),
  requestCreation: (payload) => http.post(ENDPOINTS.sellers.requests, payload),
};
