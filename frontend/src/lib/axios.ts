import axios, {
    type AxiosError,
    type AxiosRequestConfig,
    type InternalAxiosRequestConfig,
} from 'axios';
import { parseError } from '../shared/utils/errorUtils';
import { useAuthStore } from '../store/authStore';

/**
 * Centralized Axios instance for all API calls.
 * It Configures the base URL and default headers and attaches authentication token
 * automatically.
 * On 401: attempt a silent token refresh before giving up.
 * Serialize concurrent 401s so only one refresh call is made.
 * Handle global auth failures (expired refresh token, etc.).
 */
export const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
    headers: { 'Content-Type': 'application/json' },
    timeout: 15_000,
});

/**
 * While a refresh is in flight, every other 401-failed request is pushed here.
 * Once the refresh settles, all queued requests are retried (or rejected).
 */
type QueueEntry = {
    resolve: (token: string) => void;
    reject: (err: unknown) => void;
};

let isRefreshing = false;
let pendingQueue: QueueEntry[] = [];

function processQueue(error: unknown, token: string | null) {
    pendingQueue.forEach((entry) => {
        if (error) {
            entry.reject(error);
        } else {
            entry.resolve(token!);
        }
    });
    pendingQueue = [];
}

// Request interceptor — attach access token
apiClient.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = useAuthStore.getState().accessToken;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(parseError(error)),
);

// Response interceptor — silent refresh on 401
apiClient.interceptors.response.use(
    (response) => response,

    async (error: AxiosError) => {
        const originalRequest = error.config as AxiosRequestConfig & {
            _retry?: boolean;
        };

        // Only attempt refresh for 401 responses that haven't already been retried.
        // Also skip the refresh-token endpoint itself — if that 401s, we're done.
        const is401 = error.response?.status === 401;
        const isRefreshEndpoint = originalRequest.url?.includes('/auth/refresh-token');

        if (!is401 || originalRequest._retry || isRefreshEndpoint) {
            return Promise.reject(parseError(error));
        }

        const { refreshToken, setAuth, clearAuth } = useAuthStore.getState();

        // No refresh token stored → go straight to login
        if (!refreshToken) {
            clearAuth();
            window.dispatchEvent(new CustomEvent('auth:unauthorized'));
            return Promise.reject(parseError(error));
        }

        // If another refresh is already in flight, queue this request
        if (isRefreshing) {
            return new Promise<string>((resolve, reject) => {
                pendingQueue.push({ resolve, reject });
            })
                .then((newAccessToken) => {
                    originalRequest._retry = true;
                    if (originalRequest.headers) {
                        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                    } else {
                        originalRequest.headers = { Authorization: `Bearer ${newAccessToken}` };
                    }
                    return apiClient(originalRequest);
                })
                .catch((err) => Promise.reject(parseError(err)));
        }

        // First 401 — kick off the refresh
        originalRequest._retry = true;
        isRefreshing = true;

        try {
            const response = await axios.post(
                `${apiClient.defaults.baseURL}/auth/refresh-token`,
                { refreshToken },
                { headers: { 'Content-Type': 'application/json' } },
            );

            // Backend wraps in ResponseWrapper<TokenResponse>
            const tokenData = response.data?.data;
            const newAccessToken: string = tokenData.accessToken;
            const newRefreshToken: string = tokenData.refreshToken;

            // Persist the new token pair
            const { user } = useAuthStore.getState();
            if (user) {
                setAuth(user, newAccessToken, newRefreshToken);
            }

            // Update the default header for future requests
            apiClient.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;

            // Unblock all queued requests
            processQueue(null, newAccessToken);

            // Retry the original request with the new token
            if (originalRequest.headers) {
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
            } else {
                originalRequest.headers = { Authorization: `Bearer ${newAccessToken}` };
            }
            return apiClient(originalRequest);
        } catch (refreshError) {
            // Refresh failed (token revoked, expired, network error, etc.)
            processQueue(refreshError, null);
            clearAuth();
            window.dispatchEvent(new CustomEvent('auth:unauthorized'));
            return Promise.reject(parseError(refreshError as AxiosError));
        } finally {
            isRefreshing = false;
        }
    },
);