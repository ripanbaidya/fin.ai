import { apiClient } from "../../lib/axios";
import type { PaginatedData, ResponseWrapper } from "../../types/api.types";
import type {
  CreateTransactionRequest,
  TransactionSortField,
  TxnType,
  TransactionResponse,
  UpdateTransactionRequest
} from "./transaction.types";

export const transactionService = {
  getAll: async (params?: {
    type?: TxnType;
    categoryId?: string;
    dateFrom?: string;
    dateTo?: string;
    page?: number;
    size?: number;
    sortBy?: TransactionSortField;
    direction?: "ASC" | "DESC";
  }): Promise<ResponseWrapper<PaginatedData<TransactionResponse>>> => {
    const res = await apiClient.get<ResponseWrapper<PaginatedData<TransactionResponse>>>('/transactions', { params });
    return res.data;
  },

  getById: async (id: string): Promise<ResponseWrapper<TransactionResponse>> => {
    const res = await apiClient.get<ResponseWrapper<TransactionResponse>>(`/transactions/${id}`);
    return res.data;
  },

  create: async (data: CreateTransactionRequest): Promise<ResponseWrapper<TransactionResponse>> => {
    const res = await apiClient.post<ResponseWrapper<TransactionResponse>>('/transactions', data);
    return res.data;
  },

  update: async (id: string, data: UpdateTransactionRequest): Promise<ResponseWrapper<TransactionResponse>> => {
    const res = await apiClient.put<ResponseWrapper<TransactionResponse>>(`/transactions/${id}`, data);
    return res.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete<void>(`/transactions/${id}`);
  },

  /* Export transaction as csv format */
  exportCsv: async (): Promise<void> => {
    const res = await apiClient.get<Blob>('/transactions/export/csv',
      { responseType: 'blob' }
    );
    const url = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement('a');
    link.href = url;
    const today = new Date().toISOString().split('T')[0];
    link.setAttribute('download', `fin.ai-transactions-${today}.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  }
}