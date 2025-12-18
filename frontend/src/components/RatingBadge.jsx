import { formatRating } from '../utils/format';

export default function RatingBadge({ value, size = 'md' }) {
  return (
    <span className={`rating-badge rating-badge--${size}`} aria-label={`Rating ${formatRating(value)} out of 5`}>
      <span aria-hidden="true">★</span>
      {formatRating(value)}
    </span>
  );
}
