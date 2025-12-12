const SESSION_KEY = 'rating-system.session';

export const sessionStorageService = {
  read() {
    try {
      const raw = localStorage.getItem(SESSION_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },

  write(session) {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  },

  clear() {
    localStorage.removeItem(SESSION_KEY);
  },
};
