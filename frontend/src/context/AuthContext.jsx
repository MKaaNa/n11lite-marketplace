import { createContext, useContext, useEffect, useState } from 'react';
import { getCurrentUser, login, register, verifyLogin } from '../api/authApi';
import { getStoredToken, removeStoredToken, saveToken } from '../api/apiClient';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(getStoredToken());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadUser() {
      const savedToken = getStoredToken();

      if (!savedToken) {
        setLoading(false);
        return;
      }

      try {
        const response = await getCurrentUser();
        setUser(response.data);
        setToken(savedToken);
      } catch {
        removeStoredToken();
        setUser(null);
        setToken(null);
      } finally {
        setLoading(false);
      }
    }

    loadUser();
  }, []);

  async function registerUser(request) {
    const response = await register(request);
    return response.data;
  }

  async function startLogin(request) {
    const response = await login(request);
    return response.data;
  }

  async function verifyLoginCode(request) {
    const response = await verifyLogin(request);
    const jwtResponse = response.data;

    saveToken(jwtResponse.token);
    setToken(jwtResponse.token);
    setUser(jwtResponse.user);

    return jwtResponse;
  }

  function logout() {
    removeStoredToken();
    setToken(null);
    setUser(null);
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        registerUser,
        startLogin,
        verifyLoginCode,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
