export interface PaymentModeResponse {
    id: string;
    name: string;
    isDefault: boolean;
}

export interface CreatePaymentModeRequest {
    name: string;
}

export interface UpdatePaymentModeRequest {
    name: string;
}