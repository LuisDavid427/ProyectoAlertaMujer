import { createContext, useContext, useMemo, useState } from 'react';
import { isAuthenticated as checkAuth } from '../services/authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [isAuth, setIsAuth] = useState(() => checkAuth());

    const login = (token) => {
        localStorage.setItem('token', token);
        sessionStorage.setItem('sesionAlertaMujer', 'activa');
        setIsAuth(true);
    };

    const logout = () => {
        localStorage.removeItem('token');
        sessionStorage.removeItem('sesionAlertaMujer');
        sessionStorage.removeItem('token');
        setIsAuth(false);
    };

    const value = useMemo(() => ({
        isAuthenticated: isAuth,
        login,
        logout,
    }), [isAuth]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth debe usarse dentro de AuthProvider');
    }
    return context;
}
