import { apiClient } from "../../lib/axios";
import type { ResponseWrapper } from "../../types/api.types";
import type {
  CreateOrderApiResponse,
  PaymentHistoryApiResponse,
  PlansResponse,
  SubscriptionPlan,
  SubscriptionStatusApiResponse,
  VerifyPaymentRequest,
} from "./subscription.types";

export const subscriptionService = {
  getPlans: async (): Promise<PlansResponse> => {
    const response =
      await apiClient.get<PlansResponse>("/payments/plans");
    return response.data;
  },

  getStatus: async (): Promise<SubscriptionStatusApiResponse> => {
    const response = await apiClient.get<SubscriptionStatusApiResponse>(
      "/subscriptions/status",
    );
    return response.data;
  },

  getPaymentHistory: async (): Promise<PaymentHistoryApiResponse> => {
    const response = await apiClient.get<PaymentHistoryApiResponse>(
      "/payments/history",
    );
    return response.data;
  },

  createOrder: async (
    plan: SubscriptionPlan,
  ): Promise<CreateOrderApiResponse> => {
    const response = await apiClient.post<CreateOrderApiResponse>(
      "/payments/orders",
      { plan },
    );
    return response.data;
  },

  verifyPayment: async (
    request: VerifyPaymentRequest,
  ): Promise<ResponseWrapper<null>> => {
    const response = await apiClient.post<ResponseWrapper<null>>(
      "/payments/verify",
      request,
    );
    return response.data;
  },
};
