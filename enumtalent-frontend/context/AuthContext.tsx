'use client';
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authAPI } from '@/lib/api';
import {AuthContextType, AuthResponse, User} from "@/type/auth";


const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
    const [user, setUser] = useState<User | null>(null);
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

    const signup = async (email: string, password: string): Promise<AuthResponse> => {
        try {
            console.log('Sending signup request for:', email);
            const response = await authAPI.signup({ email, password });
            console.log('Signup response:', response.data);
            return { success: true, data: response.data };
        } catch (error: any) {
            console.error('Signup error details:', error);
            console.error('Error response:', error.response);
            return {
                success: false,
                error: error.response?.data?.message || 'Signup failed'
            };
        }
    };

    const login = async (email: string, password: string): Promise<AuthResponse> => {
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
        } catch (error: any) {
            return {
                success: false,
                error: error.response?.data?.message || 'Login failed'
            };
        }
    };

    const logout = async (): Promise<void> => {
        try {
            if (user?.userId) {
                await authAPI.logout(user.userId);
            }
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            localStorage.removeItem('userId');
            localStorage.removeItem('authToken');
            setUser(null);
        }
    };

    const value: AuthContextType = {
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