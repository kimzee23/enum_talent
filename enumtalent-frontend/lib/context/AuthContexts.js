'use client';
import { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '@/lib/api';

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (typeof window !== 'undefined') {
            const token = localStorage.getItem('authToken');
            const userId = localStorage.getItem('userId');

            if (token && userId) {
                setUser({ userId, token });
            }
        }
        setLoading(false);
    }, []);

    const signup = async (email, password) => {
        try {
            const response = await authAPI.signup({ email, password });
            return { success: true, data: response.data };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Signup failed'
            };
        }
    };

    const login = async (email, password) => {
        try {
            const response = await authAPI.login({ email, password });
            const message = response.data.message;

            const userIdMatch = message.match(/userId=([a-f0-9]+)/);
            if (userIdMatch) {
                const userId = userIdMatch[1];

                localStorage.setItem('userId', userId);
                localStorage.setItem('authToken', 'dummy-token');

                setUser({ userId, token: 'dummy-token' });
            }

            return { success: true, data: response.data };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Login failed'
            };
        }
    };

    const logout = async () => {
        try {
            if (user?.userId) {
                await authAPI.logout(user.userId);
            }
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            // Clear storage
            localStorage.removeItem('userId');
            localStorage.removeItem('authToken');
            setUser(null);
        }
    };

    const value = {
        user,
        signup,
        login,
        logout,
        loading
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};