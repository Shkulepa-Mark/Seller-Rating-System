import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { adminApi } from '../api/admin';
import { authApi } from '../api/auth';
import { setUnauthorizedHandler } from '../api/client';
import { ROLES } from '../constants/roles';
import { sessionStorageService } from '../utils/storage';

const AuthContext = createContext(null);

async function resolveRole() {
  try {
    await adminApi.getPendingSellers();
    return ROLES.admin;
  } catch (error) {
    if (error.status === 403) {
      return ROLES.seller;
    }
    throw error;
  }
}

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => sessionStorageService.read());

  const logout = useCallback(() => {
    sessionStorageService.clear();
    setSession(null);
  }, []);

  const login = useCallback(async (credentials) => {
    const { email, token } = await authApi.login(credentials);

    sessionStorageService.write({ token, user: { email, role: null } });

    try {
      const nextSession = { token, user: { email, role: await resolveRole() } };
      sessionStorageService.write(nextSession);
      setSession(nextSession);
      return nextSession.user;
    } catch (error) {
      sessionStorageService.clear();
      throw error;
    }
  }, []);

  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(null);
  }, [logout]);

  const value = useMemo(
    () => ({
      user: session?.user ?? null,
      isAuthenticated: Boolean(session?.token),
      isAdmin: session?.user?.role === ROLES.admin,
      isSeller: session?.user?.role === ROLES.seller,
      login,
      logout,
    }),
    [session, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
