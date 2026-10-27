import { http } from './client';
import { ENDPOINTS } from './endpoints';

export const gamesApi = {
  list: (signal) => http.get(ENDPOINTS.games, { signal }),
};
