'use client';
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authAPI, profileAPI } from '@/lib/api';
import { AuthContextType, AuthResponse, User } from "@/types/auth";

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
            return {
                success: true,
                data: response.data,
                redirectTo: '/login'
            };
        } catch (error: any) {
            console.error('Signup error details:', error);
            console.error('Error response:', error.response);
            return {
                success: false,
                error: error.response?.data?.message || 'Signup failed'
            };
        }
    };


    const isProfileComplete = (profileData: any): boolean => {
        if (!profileData) return false;

        const requiredFields = [
            'firstName', 'lastName', 'phone', 'location', 'bio',
            'headline', 'skills', 'experienceLevel', 'highestDegree',
            'institution', 'fieldOfStudy', 'graduationYear'
        ];

        const completedFields = requiredFields.filter(field => {
            const value = profileData[field];
            if (Array.isArray(value)) {
                return value.length > 0;
            }
            if (field === 'graduationYear') {
                return value !== null && value !== 0;
            }
            return Boolean(value);
        });

        return completedFields.length >= requiredFields.length * 0.8; // 80% complete
    };

    const login = async (email: string, password: string): Promise<AuthResponse> => {
        try {
            console.log('Sending login request for:', email);
            const response = await authAPI.login({ email, password });
            console.log('Login response:', response.data);

            const message = response.data.message;

            const userIdMatch = message.match(/userId=([a-f0-9-]+)/i);
            if (userIdMatch) {
                const userId = userIdMatch[1];

                // Store in localStorage
                localStorage.setItem('userId', userId);
                localStorage.setItem('authToken', 'dummy-token');

                // Update user state
                setUser({ userId, token: 'dummy-token' });

                console.log('User logged in successfully, userId:', userId);

                // Check if user has a complete profile
                try {
                    console.log('Checking for existing profile...');
                    const profileResponse = await profileAPI.getProfile(userId);
                    console.log('Profile check response:', profileResponse.data);

                    if (profileResponse.data && isProfileComplete(profileResponse.data)) {
                        console.log('✅ Profile is complete, redirecting to profile page');
                        return {
                            success: true,
                            data: response.data,
                            redirectTo: '/profile'
                        };
                    } else if (profileResponse.data) {
                        console.log('⚠️ Profile exists but incomplete, redirecting to dashboard');
                        return {
                            success: true,
                            data: response.data,
                            redirectTo: '/dashboard'
                        };
                    } else {
                        console.log('❌ No profile found, redirecting to dashboard');
                        return {
                            success: true,
                            data: response.data,
                            redirectTo: '/dashboard'
                        };
                    }
                } catch (profileError) {
                    console.log('❌ Error checking profile, redirecting to dashboard:', profileError);
                    // If profile check fails, assume no profile and go to dashboard
                    return {
                        success: true,
                        data: response.data,
                        redirectTo: '/dashboard'
                    };
                }
            }

            // Fallback if no userId found
            console.log('No userId found in response, redirecting to dashboard');
            return {
                success: true,
                data: response.data,
                redirectTo: '/dashboard'
            };
        } catch (error: any) {
            console.error('Login error details:', error);

            if (error.response) {
                console.error('Error response status:', error.response.status);
                console.error('Error response data:', error.response.data);
                return {
                    success: false,
                    error: error.response?.data?.message || `Login failed: ${error.response.status}`
                };
            } else if (error.request) {
                console.error('No response received:', error.request);
                return {
                    success: false,
                    error: 'No response from server. Please check if the backend is running.'
                };
            } else {
                console.error('Error message:', error.message);
                return {
                    success: false,
                    error: error.message || 'Login failed'
                };
            }
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