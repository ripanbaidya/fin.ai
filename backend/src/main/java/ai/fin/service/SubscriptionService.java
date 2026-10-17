package ai.fin.service;

import ai.fin.dto.payment.*;

import java.util.List;

public interface SubscriptionService {

    List<PlanResponse> getPlans();

    CreateOrderResponse createOrder(String userId, CreateOrderRequest request);

    void verifyPayment(String userId, VerifyPaymentRequest request);

    void handleWebhook(String payload, String signature);

    SubscriptionStatusResponse getSubscriptionStatus(String userId);

    List<PaymentHistoryResponse> getPaymentHistory(String userId);
}
