import type { ApiErrorDetail, ErrorResponse, ErrorType } from '../types/api.types';

export class AppError extends Error {
    readonly type: ErrorType;
    readonly code: string;
    readonly status: number;
    readonly path: string;
    readonly timestamp: string;
    readonly fieldErrors: ApiErrorDetail[];

    constructor(response: ErrorResponse) {
        super(response.message);
        this.name = 'AppError';
        this.type = getErrorType(response.status, response.code, (response.errors?.length ?? 0) > 0);
        this.code = response.code;
        this.status = response.status;
        this.path = response.meta.path;
        this.timestamp = response.meta.timestamp;
        this.fieldErrors = response.errors ?? [];
    }

    // Convenience predicates
    get isValidation() {
        return this.type === 'VALIDATION';
    }

    get isAuthentication() {
        return this.type === 'AUTHENTICATION';
    }

    get isAuthorization() {
        return this.type === 'AUTHORIZATION';
    }

    get isNotFound() {
        return this.type === 'NOT_FOUND';
    }

    get isConflict() {
        return this.type === 'CONFLICT';
    }

    get isBusiness() {
        return this.type === 'BUSINESS';
    }

    get isServerError() {
        return this.type === 'INTERNAL' || this.type === 'SERVICE_UNAVAILABLE';
    }

    // Get error for a specific field (for forms)
    getFieldError(field: string): string | undefined {
        return this.fieldErrors.find(e => e.field === field)?.reason;
    }

    // Convert field errors to a map: { email: 'must not be blank', ... }
    toFieldErrorMap(): Record<string, string> {
        return Object.fromEntries(this.fieldErrors.map(e => [e.field, e.reason]));
    }
}

function getErrorType(status: number, code: string, hasFieldErrors: boolean): ErrorType {
    if (status === 0 || status === 503) return 'SERVICE_UNAVAILABLE';
    if (
        hasFieldErrors ||
        ['VALIDATION_FAILED', 'MALFORMED_JSON', 'MISSING_PARAMETER', 'TYPE_MISMATCH'].includes(code)
    ) return 'VALIDATION';
    if (code === 'INVALID_AUTH_HEADER') return 'AUTHENTICATION';
    if (status === 401) return 'AUTHENTICATION';
    if (status === 403) return 'AUTHORIZATION';
    if (status === 404 || status === 410) return 'NOT_FOUND';
    if (status === 409) return 'CONFLICT';
    if (status >= 500) return 'INTERNAL';
    return 'BUSINESS';
}