import { useCallback, useState } from 'react';

export function useSubmit(action) {
  const [pending, setPending] = useState(false);
  const [error, setError] = useState(null);

  const submit = useCallback(
    async (...args) => {
      setPending(true);
      setError(null);
      try {
        return await action(...args);
      } catch (caught) {
        setError(caught);
        return undefined;
      } finally {
        setPending(false);
      }
    },
    [action],
  );

  return { submit, pending, error };
}
