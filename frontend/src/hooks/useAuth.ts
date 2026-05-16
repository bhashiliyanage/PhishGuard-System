import { useState, useEffect } from 'react';
import keycloak from '../lib/keycloak';
import api from '../lib/api';
import type { AppUser } from '../types';

interface AuthState {
  initialized: boolean;
  authenticated: boolean;
  user: AppUser | null;
  roles: string[];
}

export function useAuth() {
  const [state, setState] = useState<AuthState>({
    initialized: false,
    authenticated: false,
    user: null,
    roles: [],
  });

  useEffect(() => {
    keycloak
      .init({ onLoad: 'login-required', checkLoginIframe: false })
      .then(async (authenticated) => {
        if (authenticated) {
          const roles = keycloak.realmAccess?.roles ?? [];
          const user = await api.post<AppUser>('/user/sync').then((r) => r.data);
          setState({ initialized: true, authenticated, user, roles });
        } else {
          setState({ initialized: true, authenticated: false, user: null, roles: [] });
        }
      })
      .catch(() => {
        setState({ initialized: true, authenticated: false, user: null, roles: [] });
      });
  }, []);

  const logout = () => keycloak.logout({ redirectUri: window.location.origin });
  const isAdmin = state.roles.includes('admin');

  return { ...state, logout, isAdmin };
}
