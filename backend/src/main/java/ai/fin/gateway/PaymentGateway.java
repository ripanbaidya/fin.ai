package ai.fin.gateway;

import ai.fin.enums.PaymentProvider;

public interface PaymentGateway {

    PaymentProvider getProvider();

    CreateOrderResult createOrder(long amountInPaise, String currency, String receiptId);

    boolean verifyPaymentSignature(String orderId, String paymentId, String signature);

    boolean verifyWebhookSignature(String payload, String signature);

    record CreateOrderResult(String orderId, long amount, String currency) {
    }
}
