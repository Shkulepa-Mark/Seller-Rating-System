import { useCallback, useEffect, useState } from 'react';

export function useFetch(fetcher) {
  const [state, setState] = useState({ data: null, error: null, loading: true });
  const [version, setVersion] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    setState((previous) => ({ ...previous, loading: true, error: null }));

    fetcher(controller.signal)
      .then((data) => setState({ data, error: null, loading: false }))
      .catch((error) => {
        if (error.name !== 'AbortError') {
          setState({ data: null, error, loading: false });
        }
      });

    return () => controller.abort();
  }, [fetcher, version]);

  const reload = useCallback(() => setVersion((value) => value + 1), []);

  return { ...state, reload };
}
