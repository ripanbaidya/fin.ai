import { apiClient } from "../../lib/axios";
import type { ResponseWrapper } from "../../types/api.types";
import type {
  CreatePaymentModeRequest,
  PaymentModeResponse,
  UpdatePaymentModeRequest
} from "./paymentMode.types";

export const paymentModeService = {
  create: async (data: CreatePaymentModeRequest): Promise<ResponseWrapper<PaymentModeResponse>> => {
    const res = await apiClient.post<ResponseWrapper<PaymentModeResponse>>('/payment-modes', data);
    return res.data;
  },

  getAll: async (): Promise<ResponseWrapper<PaymentModeResponse[]>> => {
    const res = await apiClient.get<ResponseWrapper<PaymentModeResponse[]>>('/payment-modes');
    return res.data;
  },

  update: async (id: string, data: UpdatePaymentModeRequest): Promise<ResponseWrapper<PaymentModeResponse>> => {
    const res = await apiClient.put<ResponseWrapper<PaymentModeResponse>>(`/payment-modes/${id}`, data);
    return res.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete<void>(`/payment-modes/${id}`);
  }
}