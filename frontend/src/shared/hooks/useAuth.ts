import { useAuthStore } from '../../store/authStore';

export const useAuth = () => {
    const { user, accessToken, refreshToken, isAdmin, setAuth, setIsAdmin, clearAuth } = useAuthStore();
    return {
        user,
        accessToken,
        refreshToken,
        isAdmin,
        isAuthenticated: !!user && !!accessToken,
        setAuth,
        setIsAdmin,
        logout: clearAuth,
    };
};