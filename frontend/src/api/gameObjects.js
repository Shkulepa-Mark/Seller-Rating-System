import { http } from './client';
import { ENDPOINTS } from './endpoints';

export const gameObjectsApi = {
  list: (signal) => http.get(ENDPOINTS.gameObjects.collection, { signal }),
  create: (payload) => http.post(ENDPOINTS.gameObjects.collection, payload),
  update: (id, payload) => http.put(ENDPOINTS.gameObjects.byId(id), payload),
  remove: (id) => http.delete(ENDPOINTS.gameObjects.byId(id)),
};
