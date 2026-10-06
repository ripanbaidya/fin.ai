export type TxnType = 'INCOME' | 'EXPENSE';
export type TransactionSortField = 'DATE' | 'AMOUNT' | 'TYPE' | 'CREATED_AT';

export interface TransactionResponse {
    id: string;
    amount: number;
    type: TxnType;
    date: string;
    note: string | null;
    categoryId: string | null;
    categoryName: string | null;
    paymentModeId: string | null;
    paymentModeName: string | null;
    embeddingId: string | null;
}

export interface CreateTransactionRequest {
    amount: number;
    type: TxnType;
    date: string;
    note?: string;
    categoryId: string;
    paymentModeId: string;
}

export interface UpdateTransactionRequest {
    amount: number;
    type?: TxnType;
    date?: string;
    note?: string;
    categoryId?: string;
    paymentModeId?: string;
}
