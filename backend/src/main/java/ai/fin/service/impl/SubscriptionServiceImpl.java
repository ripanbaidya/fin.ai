package ai.fin.service.impl;

import ai.fin.config.properties.ChatProperties;
import ai.fin.config.properties.PaymentProperties;
import ai.fin.dto.payment.*;
import ai.fin.entities.PaymentTransaction;
import ai.fin.entities.Subscription;
import ai.fin.entities.User;
import ai.fin.enums.PaymentProvider;
import ai.fin.enums.PaymentStatus;
import ai.fin.enums.SubscriptionPlan;
import ai.fin.enums.SubscriptionStatus;
import ai.fin.gateway.PaymentGateway;
import ai.fin.gateway.PaymentGatewayRegistry;
import ai.fin.repository.PaymentTransactionRepository;
import ai.fin.repository.SubscriptionRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.SubscriptionService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.BusinessException;
import ai.fin.shared.exception.types.PaymentException;
import ai.fin.shared.exception.types.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final ChatProperties chatProperties;
    private final PaymentProperties paymentProperties;

    private final PaymentGatewayRegistry gatewayRegistry;

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> getPlans() {
        return paymentProperties.plans().entrySet().stream()
                .map(entry -> new PlanResponse(
                        SubscriptionPlan.valueOf(entry.getKey()),
                        entry.getValue().priceInPaise(),
                        entry.getValue().durationDays()
                ))
                .toList();
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(String userId, CreateOrderRequest request) {
        SubscriptionPlan plan = request.plan();

        // If user already has an active subscription, allow only upgrades (higher-tier plan)
        subscriptionRepository.findByUser_IdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .ifPresent(existing -> {
                    if (isActiveSubscription(existing)) {
                        throw new BusinessException(
                                ErrorCode.SUBSCRIPTION_ALREADY_ACTIVE,
                                "You already have an active " + existing.getPlan() + " subscription."
                        );
                    }
                });

        PaymentProperties.PlanConfig planConfig = getPlanConfig(plan);
        String currency = paymentProperties.razorpay().currency();

        PaymentGateway gateway = gatewayRegistry.get(PaymentProvider.RAZORPAY);
        PaymentGateway.CreateOrderResult result = gateway.createOrder(
                planConfig.priceInPaise(), currency, "sub_" + userId + "_" + System.currentTimeMillis()
        );

        User user = getUser(userId);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUser(user);
        transaction.setPlan(plan);
        transaction.setProvider(PaymentProvider.RAZORPAY);
        transaction.setProviderOrderId(result.orderId());
        transaction.setAmountInPaise(planConfig.priceInPaise());
        transaction.setCurrency(currency);
        transaction.setStatus(PaymentStatus.CREATED);
        paymentTransactionRepository.save(transaction);

        log.info("Order created: orderId={}, plan={}, userId={}", result.orderId(), plan, userId);

        return new CreateOrderResponse(result.orderId(), planConfig.priceInPaise(), currency, paymentProperties.razorpay().keyId());
    }

    @Override
    @Transactional
    public void verifyPayment(String userId, VerifyPaymentRequest request) {
        PaymentTransaction transaction = paymentTransactionRepository
                .findByProviderOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new PaymentException(
                        ErrorCode.SUBSCRIPTION_NOT_FOUND,
                        "No order found for the given order ID.")
                );


        // Idempotency: if already captured, skip
        if (transaction.getStatus() == PaymentStatus.CAPTURED) {
            log.info("Payment already verified: orderId={}", request.razorpayOrderId());
            return;
        }

        PaymentGateway gateway = gatewayRegistry.get(PaymentProvider.RAZORPAY);
        boolean valid = gateway.verifyPaymentSignature(
                request.razorpayOrderId(),
                request.razorpayPaymentId(),
                request.razorpaySignature()
        );

        if (!valid) {
            transaction.setStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(transaction);
            throw new PaymentException(ErrorCode.PAYMENT_SIGNATURE_INVALID);
        }

        transaction.setProviderPaymentId(request.razorpayPaymentId());
        transaction.setStatus(PaymentStatus.CAPTURED);
        paymentTransactionRepository.save(transaction);

        activateSubscription(transaction);

        log.info("Payment verified: orderId={}, paymentId={}", request.razorpayOrderId(), request.razorpayPaymentId());
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        PaymentGateway gateway = gatewayRegistry.get(PaymentProvider.RAZORPAY);

        if (signature != null && !signature.isBlank()) {
            boolean valid = gateway.verifyWebhookSignature(payload, signature);
            if (!valid) {
                log.warn("Webhook signature verification failed");
                throw new PaymentException(
                        ErrorCode.PAYMENT_SIGNATURE_INVALID,
                        "Webhook signature verification failed."
                );
            }
        }

        JSONObject webhookPayload = new JSONObject(payload);
        String event = webhookPayload.getString("event");

        if ("payment.captured".equals(event)) {
            handlePaymentCaptured(webhookPayload);
        } else if ("payment.failed".equals(event)) {
            handlePaymentFailed(webhookPayload);
        } else {
            log.info("Ignoring webhook event: {}", event);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getSubscriptionStatus(String userId) {
        User user = getUser(userId);

        return subscriptionRepository.findByUser_IdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .filter(this::isActiveSubscription)
                .map(sub -> new SubscriptionStatusResponse(
                        true,
                        sub.getPlan(),
                        sub.getStatus(),
                        sub.getStartDate(),
                        sub.getEndDate(),
                        user.getFreeChatCount(),
                        chatProperties.freeChatLimit()
                ))
                .orElse(new SubscriptionStatusResponse(
                        false,
                        null,
                        null,
                        null,
                        null,
                        user.getFreeChatCount(),
                        chatProperties.freeChatLimit()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryResponse> getPaymentHistory(String userId) {
        return paymentTransactionRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(tx -> new PaymentHistoryResponse(
                        tx.getId(),
                        tx.getPlan(),
                        tx.getProvider(),
                        tx.getProviderOrderId(),
                        tx.getProviderPaymentId(),
                        tx.getAmountInPaise(),
                        tx.getCurrency(),
                        tx.getStatus(),
                        tx.getCreatedAt()
                ))
                .toList();
    }

    private void activateSubscription(PaymentTransaction transaction) {
        // Expire any existing active subscription
        subscriptionRepository.findByUser_IdAndStatus(transaction.getUser().getId(), SubscriptionStatus.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(SubscriptionStatus.EXPIRED);
                    subscriptionRepository.save(existing);
                });

        Subscription subscription = new Subscription();
        subscription.setUser(transaction.getUser());
        subscription.setPlan(transaction.getPlan());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(Instant.now());

        PaymentProperties.PlanConfig planConfig = getPlanConfig(transaction.getPlan());
        if (planConfig.durationDays() != null) {
            subscription.setEndDate(Instant.now().plus(planConfig.durationDays(), ChronoUnit.DAYS));
        }
        // LIFETIME: endDate remains null

        subscriptionRepository.save(subscription);
        log.info("Subscription activated: plan={}, userId={}", transaction.getPlan(), transaction.getUser().getId());
    }

    private void handlePaymentCaptured(JSONObject webhookPayload) {
        JSONObject paymentEntity = webhookPayload
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String orderId = paymentEntity.getString("order_id");
        String paymentId = paymentEntity.getString("id");

        paymentTransactionRepository.findByProviderOrderId(orderId).ifPresent(transaction -> {
            if (transaction.getStatus() == PaymentStatus.CAPTURED) {
                log.info("Webhook: payment already captured, skipping: orderId={}", orderId);
                return;
            }

            transaction.setProviderPaymentId(paymentId);
            transaction.setStatus(PaymentStatus.CAPTURED);
            paymentTransactionRepository.save(transaction);

            activateSubscription(transaction);
            log.info("Webhook: payment captured: orderId={}, paymentId={}", orderId, paymentId);
        });
    }

    private void handlePaymentFailed(JSONObject webhookPayload) {
        JSONObject paymentEntity = webhookPayload
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String orderId = paymentEntity.getString("order_id");

        paymentTransactionRepository.findByProviderOrderId(orderId).ifPresent(transaction -> {
            if (transaction.getStatus() != PaymentStatus.CREATED) {
                return;
            }
            transaction.setStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(transaction);
            log.info("Webhook: payment failed: orderId={}", orderId);
        });
    }

    private boolean isActiveSubscription(Subscription subscription) {
        return subscription.getEndDate() == null || subscription.getEndDate().isAfter(Instant.now());
    }

    private PaymentProperties.PlanConfig getPlanConfig(SubscriptionPlan plan) {
        PaymentProperties.PlanConfig config = paymentProperties.plans().get(plan.name());
        if (config == null) {
            throw new PaymentException(ErrorCode.VALIDATION_FAILED, "Invalid subscription plan: " + plan);
        }
        return config;
    }

    private User getUser(String userId) {
        return userRepository.getReferenceById(userId);
    }
}
