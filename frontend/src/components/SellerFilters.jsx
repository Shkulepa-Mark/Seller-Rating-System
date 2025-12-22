import { useGames } from '../hooks/useGames';
import FormField from './FormField';

export default function SellerFilters({ filters, onChange, onReset }) {
  const { data: games } = useGames();

  const handleChange = (event) => {
    const { name, value } = event.target;
    onChange({ ...filters, [name]: value });
  };

  return (
    <form className="filters" onSubmit={(event) => event.preventDefault()}>
      <FormField as="select" label="Game" name="gameName" value={filters.gameName} onChange={handleChange}>
        <option value="">All games</option>
        {games?.map((game) => (
          <option key={game.id} value={game.name}>
            {game.name}
          </option>
        ))}
      </FormField>
      <FormField
        label="Min rating"
        name="minRating"
        type="number"
        min="0"
        max="5"
        step="0.1"
        value={filters.minRating}
        onChange={handleChange}
      />
      <FormField
        label="Max rating"
        name="maxRating"
        type="number"
        min="0"
        max="5"
        step="0.1"
        value={filters.maxRating}
        onChange={handleChange}
      />
      <button type="button" className="btn btn--ghost" onClick={onReset}>
        Reset
      </button>
    </form>
  );
}
