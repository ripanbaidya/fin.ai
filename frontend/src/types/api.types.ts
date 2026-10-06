export interface ResponseWrapper<T> {
    success: true;
    status: number;
    code: string;
    message: string;
    data: T;
    meta: ApiResponseMeta;
}

export interface ErrorResponse {
    success: false;
    status: number;
    code: string;
    message: string;
    errors: ApiErrorDetail[];
    meta: ApiResponseMeta;
}

export interface ApiResponseMeta {
    timestamp: string;
    path: string;
    requestId: string;
}

export interface ApiErrorDetail {
    field: string;
    rejectedValue: unknown;
    reason: string;
}

export type ErrorType =
    | 'VALIDATION'
    | 'AUTHENTICATION'
    | 'AUTHORIZATION'
    | 'NOT_FOUND'
    | 'CONFLICT'
    | 'BUSINESS'
    | 'INTERNAL'
    | 'SERVICE_UNAVAILABLE';

export interface PaginationMeta {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
}

export interface PaginatedData<T> {
    items: T[];
    pagination: PaginationMeta;
}
