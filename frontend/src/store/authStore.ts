import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthUserResponse } from '../features/auth/auth.types';

interface AuthState {
    user: AuthUserResponse | null;
    accessToken: string | null;
    refreshToken: string | null;
    isAdmin: boolean;
    setAuth: (user: AuthUserResponse, access: string, refresh: string) => void;
    setIsAdmin: (isAdmin: boolean) => void;
    clearAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            user: null,
            accessToken: null,
            refreshToken: null,
            isAdmin: false,
            setAuth: (user, accessToken, refreshToken) =>
                set({ user, accessToken, refreshToken }),
            setIsAdmin: (isAdmin) =>
                set({ isAdmin }),
            clearAuth: () =>
                set({ user: null, accessToken: null, refreshToken: null, isAdmin: false }),
        }),
        {
            name: 'walletiq-auth',
            partialize: (state) => ({
                user: state.user,
                accessToken: state.accessToken,
                refreshToken: state.refreshToken,
                isAdmin: state.isAdmin,
            }),
        }
    )
);