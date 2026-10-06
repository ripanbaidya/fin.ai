import axios from 'axios';
import { AppError } from '../../api/errorParser';
import type { ErrorResponse } from '../../types/api.types';

/**
 * Converts any thrown value into an AppError.
 */
export function parseError(error: unknown): AppError {
    // It's an Axios error with our backend's ErrorResponse body
    if (axios.isAxiosError(error) && error.response?.data) {
        const data = error.response.data as ErrorResponse;
        if (data.success === false) {
            return new AppError(data);
        }
    }

    // Network error (no response at all — server down, CORS, etc.)
    if (axios.isAxiosError(error) && !error.response) {
        return new AppError({
            success: false,
            status: 0,
            code: 'NETWORK_ERROR',
            message: 'Unable to reach the server. Please check your connection.',
            errors: [],
            meta: {
                timestamp: new Date().toISOString(),
                path: '',
                requestId: '',
            },
        });
    }

    // Already an AppError (re-thrown somewhere)
    if (error instanceof AppError) return error;

    // Completely unknown error
    return new AppError({
        success: false,
        status: 500,
        code: 'UNKNOWN_ERROR',
        message: 'An unexpected error occurred.',
        errors: [],
        meta: {
            timestamp: new Date().toISOString(),
            path: '',
            requestId: '',
        },
    });
}

/**
 * Returns the user-facing message for a given error.
 * Pass an optional override to replace the backend message.
 */
export function getErrorMessage(error: AppError, override?: string): string {
    return override ?? error.message;
}