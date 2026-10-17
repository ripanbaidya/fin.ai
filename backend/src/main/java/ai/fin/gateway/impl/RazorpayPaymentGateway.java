package ai.fin.gateway.impl;

import ai.fin.config.properties.PaymentProperties;
import ai.fin.enums.PaymentProvider;
import ai.fin.gateway.PaymentGateway;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.PaymentException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.features.payment.razorpay.enabled", havingValue = "true")
public class RazorpayPaymentGateway implements PaymentGateway {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final RazorpayClient razorpayClient;
    private final PaymentProperties.RazorpayConfig razorpayConfig;

    public RazorpayPaymentGateway(PaymentProperties paymentProperties) {
        this.razorpayConfig = paymentProperties.razorpay();
        try {
            this.razorpayClient = new RazorpayClient(razorpayConfig.keyId(), razorpayConfig.keySecret());
        } catch (RazorpayException e) {
            throw new PaymentException(ErrorCode.PAYMENT_ORDER_CREATION_FAILED, "Failed to initialize Razorpay client");
        }
    }

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.RAZORPAY;
    }

    @Override
    public CreateOrderResult createOrder(long amountInPaise, String currency, String receiptId) {
        try {
            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", currency);
            options.put("receipt", receiptId);

            Order order = razorpayClient.orders.create(options);
            String orderId = order.get("id");
            log.info("Razorpay order created: orderId={}", orderId);

            return new CreateOrderResult(orderId, amountInPaise, currency);
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new PaymentException(ErrorCode.PAYMENT_ORDER_CREATION_FAILED, "Failed to create payment order");
        }
    }

    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(attributes, razorpayConfig.keySecret());
        } catch (RazorpayException e) {
            log.warn("Payment signature verification failed: orderId={}", orderId);
            return false;
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        String webhookSecret = razorpayConfig.webhookSecret();
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.warn("Webhook secret not configured, skipping webhook signature verification");
            return false;
        }
        try {
            return Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            log.warn("Webhook signature verification failed");
            return false;
        }
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new PaymentException(ErrorCode.PAYMENT_SIGNATURE_INVALID, "Failed to compute HMAC signature");
        }
    }
}
