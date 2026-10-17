import type { ResponseWrapper } from "../../types/api.types";

export type SubscriptionPlan = "MONTHLY" | "YEARLY" | "LIFETIME";
export type PaymentStatus = "CREATED" | "CAPTURED" | "FAILED";

export interface PlanResponse {
  plan: SubscriptionPlan;
  priceInPaise: number;
  durationDays: number | null;
}

export interface SubscriptionStatusResponse {
  hasActiveSubscription: boolean;
  plan: SubscriptionPlan | null;
  status: "ACTIVE" | "EXPIRED" | "CANCELLED" | null;
  startDate: string | null;
  endDate: string | null;
  freePromptsUsed: number;
  freePromptsLimit: number;
}

export interface PaymentHistoryResponse {
  id: string;
  plan: SubscriptionPlan;
  provider: "RAZORPAY";
  providerOrderId: string;
  providerPaymentId: string | null;
  amountInPaise: number;
  currency: string;
  status: PaymentStatus;
  createdAt: string;
}

export interface CreateOrderResponse {
  orderId: string;
  amountInPaise: number;
  currency: string;
  keyId: string;
}

export interface VerifyPaymentRequest {
  razorpayOrderId: string;
  razorpayPaymentId: string;
  razorpaySignature: string;
}

export type PlansResponse = ResponseWrapper<PlanResponse[]>;
export type SubscriptionStatusApiResponse =
  ResponseWrapper<SubscriptionStatusResponse>;
export type PaymentHistoryApiResponse = ResponseWrapper<
  PaymentHistoryResponse[]
>;
export type CreateOrderApiResponse = ResponseWrapper<CreateOrderResponse>;
