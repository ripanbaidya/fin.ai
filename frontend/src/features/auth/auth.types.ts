export interface LoginRequest {
    email: string;
    password: string;
}

export interface SignupRequest {
    fullName: string;
    email: string;
    password: string;
}

export interface LogoutRequest {
    refreshToken: string;
}

export interface AuthResponse {
    user: AuthUserResponse;
    token: TokenResponse;
}

export interface TokenResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresInMillis: number;
}

export interface AuthUserResponse {
    id: string;
    role: string;
    status: string;
    fullName?: string;
    email?: string;
}

export interface SendOtpRequest {
    email: string;
}

export interface VerifyOtpRequest {
    email: string;
    otp: string;
}
