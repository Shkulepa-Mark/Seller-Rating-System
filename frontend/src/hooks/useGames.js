import { gamesApi } from '../api/games';
import { useFetch } from './useFetch';

const fetchGames = (signal) => gamesApi.list(signal);

export function useGames() {
  return useFetch(fetchGames);
}
