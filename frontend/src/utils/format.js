const dateFormatter = new Intl.DateTimeFormat('en-GB', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
});

export function formatDate(value) {
  if (!value) {
    return '';
  }
  return dateFormatter.format(new Date(value));
}

export function formatRating(value) {
  return Number(value ?? 0).toFixed(1);
}

export function getFullName(person) {
  return [person?.firstName, person?.lastName].filter(Boolean).join(' ');
}

export function getInitials(name) {
  return (name ?? '')
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0].toUpperCase())
    .join('');
}
