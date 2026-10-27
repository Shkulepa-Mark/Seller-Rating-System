import { getInitials } from '../utils/format';

export default function Avatar({ name, size = 'md' }) {
  return (
    <span className={`avatar avatar--${size}`} aria-hidden="true">
      {getInitials(name) || '?'}
    </span>
  );
}
