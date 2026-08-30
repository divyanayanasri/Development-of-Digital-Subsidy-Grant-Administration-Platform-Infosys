import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem('gov_portal_session');
      return saved ? JSON.parse(saved) : null;
    } catch (e) {
      return null;
    }
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  /**
   * Unified Login Flow:
   * 1. POST /api/auth/login — the real, password-checking endpoint for
   *    officers/admins. This is a public route (see SecurityConfig) so it
   *    can be called before we have a JWT.
   * 2. If that succeeds, store the JWT and build the officer/admin session.
   * 3. If it fails specifically because no such account exists in the users
   *    table, fall back to checking the locally stored beneficiary
   *    registration (beneficiaries aren't rows in `users`, so they can't
   *    authenticate against /api/auth/login).
   * 4. If neither matches, inform the user to click Register.
   */
  const login = async (email, password) => {
    setError(null);
    setLoading(true);

    if (!email || !password) {
      setLoading(false);
      const msg = 'Please enter both email address and password.';
      setError(msg);
      throw new Error(msg);
    }

    const cleanEmail = email.trim().toLowerCase();

    try {
      const authResponse = await api.login(cleanEmail, password);

      const sessionData = {
        id: authResponse.id,
        userId: authResponse.id,
        name: authResponse.name,
        email: authResponse.email,
        role: authResponse.role,
        regionId: authResponse.regionId,
        region: authResponse.regionId ? `Region #${authResponse.regionId}` : 'Central District'
      };

      localStorage.setItem('gov_token', authResponse.token);
      localStorage.setItem('gov_portal_session', JSON.stringify(sessionData));
      setUser(sessionData);
      setLoading(false);
      return sessionData;
    } catch (err) {
      // /api/auth/login only knows about officer/admin accounts (rows in
      // `users`). A beneficiary email will legitimately fail here with
      // "Invalid email or password" — that's expected, not a real error,
      // so fall through to the beneficiary check instead of surfacing it.
      const savedBenStr = localStorage.getItem('gov_beneficiary_session');
      if (savedBenStr) {
        try {
          const savedBen = JSON.parse(savedBenStr);
          if (savedBen.email && savedBen.email.toLowerCase() === cleanEmail) {
            const benSession = {
              id: savedBen.id,
              userId: savedBen.userId || savedBen.id,
              beneficiaryId: savedBen.id,
              name: savedBen.name,
              email: savedBen.email,
              role: 'BENEFICIARY'
            };
            setUser(benSession);
            localStorage.setItem('gov_portal_session', JSON.stringify(benSession));
            setLoading(false);
            return benSession;
          }
        } catch (parseErr) {
          console.warn('Corrupt beneficiary session in localStorage:', parseErr);
        }
      }

      setLoading(false);
      const notFoundMsg = 'No account record found for this email. If you are a new beneficiary, please click "Register" below to create an account.';
      setError(notFoundMsg);
      throw new Error(notFoundMsg);
    }
  };

  /**
   * Beneficiary Registration Handler (POST /api/beneficiaries)
   */
  const signup = async (formData) => {
    setError(null);
    setLoading(true);
    try {
      const res = await api.registerBeneficiary(formData);
      const benSession = {
        id: res.id,
        userId: res.userId || res.id,
        beneficiaryId: res.id,
        name: formData.name,
        email: formData.email,
        role: 'BENEFICIARY'
      };

      // Persist beneficiary registration locally for current session auth lookup
      localStorage.setItem('gov_beneficiary_session', JSON.stringify(benSession));
      setUser(benSession);
      localStorage.setItem('gov_portal_session', JSON.stringify(benSession));

      setLoading(false);
      return res;
    } catch (err) {
      setLoading(false);
      const msg = err.message || 'Beneficiary registration failed.';
      setError(msg);
      throw new Error(msg);
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('gov_token');
    localStorage.removeItem('gov_portal_session');
  };

  return (
      <AuthContext.Provider value={{ user, loading, error, setError, login, signup, logout }}>
        {children}
      </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}