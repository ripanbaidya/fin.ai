import { apiClient } from "../../lib/axios";
import type { ResponseWrapper } from "../../types/api.types";
import type {
  RecurringTransactionResponse,
  ForecastSummaryResponse,
  CreateRecurringTransactionRequest,
  UpdateRecurringTransactionRequest,
} from "./recurring.types";

export const recurringService = {
  getAll: async (): Promise<ResponseWrapper<RecurringTransactionResponse[]>> => {
    const res = await apiClient.get<ResponseWrapper<RecurringTransactionResponse[]>>("/recurring-transactions");
    return res.data;
  },

  getById: async (
    id: string
  ): Promise<ResponseWrapper<RecurringTransactionResponse>> => {
    const res = await apiClient.get<ResponseWrapper<RecurringTransactionResponse>>(`/recurring-transactions/${id}`);
    return res.data;
  },

  create: async (
    data: CreateRecurringTransactionRequest
  ): Promise<ResponseWrapper<RecurringTransactionResponse>> => {
    const res = await apiClient.post<ResponseWrapper<RecurringTransactionResponse>>("/recurring-transactions", data);
    return res.data;
  },

  update: async (
    id: string,
    data: UpdateRecurringTransactionRequest
  ): Promise<ResponseWrapper<RecurringTransactionResponse>> => {
    const res = await apiClient.patch<ResponseWrapper<RecurringTransactionResponse>>(`/recurring-transactions/${id}`, data);
    return res.data;
  },

  // Soft-delete — backend sets isActive = false
  deactivate: async (id: string): Promise<void> => {
    await apiClient.delete<void>(`/recurring-transactions/${id}`);
  },

  getForecast: async (
    days: number = 30
  ): Promise<ResponseWrapper<ForecastSummaryResponse>> => {
    const res = await apiClient.get<ResponseWrapper<ForecastSummaryResponse>>("/recurring-transactions/forecast", {
      params: { days },
    });
    return res.data;
  },
};