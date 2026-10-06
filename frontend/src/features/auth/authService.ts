import { apiClient } from '../../lib/axios';
import type { ResponseWrapper } from '../../types/api.types';
import type {
    AuthResponse,
    LoginRequest,
    SignupRequest,
    SendOtpRequest,
    VerifyOtpRequest,
    LogoutRequest
} from './auth.types';

export const authService = {
    login: async (data: LoginRequest): Promise<ResponseWrapper<AuthResponse>> => {
        const res = await apiClient.post<ResponseWrapper<AuthResponse>>('/auth/login', data);
        return res.data;
    },

    signup: async (data: SignupRequest): Promise<ResponseWrapper<AuthResponse>> => {
        const res = await apiClient.post<ResponseWrapper<AuthResponse>>('/auth/register', data);
        return res.data;
    },

    logout: async (data: LogoutRequest): Promise<void> => {
        await apiClient.post('/users/logout', data);
    },

    sendOtp: async (data: SendOtpRequest): Promise<void> => {
        await apiClient.post('/auth/send-otp', data);
    },

    resendOtp: async (data: SendOtpRequest): Promise<void> => {
        await apiClient.post('/auth/resend-otp', data);
    },

    verifyOtp: async (data: VerifyOtpRequest): Promise<void> => {
        await apiClient.post('/auth/verify-otp', data);
    },
};
