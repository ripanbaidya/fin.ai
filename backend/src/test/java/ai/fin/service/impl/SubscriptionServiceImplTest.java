package ai.fin.service.impl;

import ai.fin.config.properties.ChatProperties;
import ai.fin.config.properties.PaymentProperties;
import ai.fin.dto.payment.*;
import ai.fin.entities.PaymentTransaction;
import ai.fin.entities.Subscription;
import ai.fin.entities.User;
import ai.fin.enums.*;
import ai.fin.gateway.PaymentGateway;
import ai.fin.gateway.PaymentGatewayRegistry;
import ai.fin.repository.PaymentTransactionRepository;
import ai.fin.repository.SubscriptionRepository;
import ai.fin.repository.UserRepository;
import ai.fin.shared.exception.types.BusinessException;
import ai.fin.shared.exception.types.PaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private PaymentProperties paymentProperties;

    @Mock
    private ChatProperties chatProperties;

    @Mock
    private PaymentGatewayRegistry gatewayRegistry;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return all configured plans")
    void getPlans_shouldReturnAllPlans() {
        when(paymentProperties.plans()).thenReturn(Map.of(
                "MONTHLY", new PaymentProperties.PlanConfig(29900L, 30),
                "YEARLY", new PaymentProperties.PlanConfig(299900L, 365),
                "LIFETIME", new PaymentProperties.PlanConfig(999900L, null)
        ));

        var plans = subscriptionService.getPlans();

        assertThat(plans).hasSize(3);
    }

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_shouldCreateOrder() {
        var planConfig = new PaymentProperties.PlanConfig(29900L, 30);
        var razorpayConfig = new PaymentProperties.RazorpayConfig(true, "key_test", "secret_test", "INR", null);

        when(subscriptionRepository.findByUser_IdAndStatus(anyString(), any())).thenReturn(Optional.empty());
        when(paymentProperties.plans()).thenReturn(Map.of("MONTHLY", planConfig));
        when(paymentProperties.razorpay()).thenReturn(razorpayConfig);
        when(gatewayRegistry.get(PaymentProvider.RAZORPAY)).thenReturn(paymentGateway);
        when(paymentGateway.createOrder(anyLong(), anyString(), anyString()))
                .thenReturn(new PaymentGateway.CreateOrderResult("order_123", 29900L, "INR"));
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));
        when(paymentTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderResponse response = subscriptionService.createOrder("user1", new CreateOrderRequest(SubscriptionPlan.MONTHLY));

        assertThat(response.orderId()).isEqualTo("order_123");
        assertThat(response.amountInPaise()).isEqualTo(29900L);
        assertThat(response.currency()).isEqualTo("INR");
        verify(paymentTransactionRepository).save(any(PaymentTransaction.class));
    }

    @Test
    @DisplayName("Should reject order when user has active subscription")
    void createOrder_shouldRejectWhenActiveSubscription() {
        Subscription activeSub = new Subscription();
        activeSub.setPlan(SubscriptionPlan.MONTHLY);
        activeSub.setStatus(SubscriptionStatus.ACTIVE);
        activeSub.setEndDate(Instant.now().plus(10, ChronoUnit.DAYS));

        when(subscriptionRepository.findByUser_IdAndStatus("user1", SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(activeSub));

        assertThatThrownBy(() -> subscriptionService.createOrder("user1", new CreateOrderRequest(SubscriptionPlan.YEARLY)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Should verify payment and activate subscription")
    void verifyPayment_shouldActivateSubscription() {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUser(testUser);
        transaction.setPlan(SubscriptionPlan.MONTHLY);
        transaction.setProvider(PaymentProvider.RAZORPAY);
        transaction.setProviderOrderId("order_123");
        transaction.setStatus(PaymentStatus.CREATED);

        var planConfig = new PaymentProperties.PlanConfig(29900L, 30);

        when(paymentTransactionRepository.findByProviderOrderId("order_123")).thenReturn(Optional.of(transaction));
        when(gatewayRegistry.get(PaymentProvider.RAZORPAY)).thenReturn(paymentGateway);
        when(paymentGateway.verifyPaymentSignature("order_123", "pay_123", "sig_123")).thenReturn(true);
        when(paymentTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(subscriptionRepository.findByUser_IdAndStatus(any(), any())).thenReturn(Optional.empty());
        when(paymentProperties.plans()).thenReturn(Map.of("MONTHLY", planConfig));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        subscriptionService.verifyPayment("user1", new VerifyPaymentRequest("order_123", "pay_123", "sig_123"));

        assertThat(transaction.getStatus()).isEqualTo(PaymentStatus.CAPTURED);
        assertThat(transaction.getProviderPaymentId()).isEqualTo("pay_123");
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("Should throw on invalid payment signature")
    void verifyPayment_shouldThrowOnInvalidSignature() {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setStatus(PaymentStatus.CREATED);

        when(paymentTransactionRepository.findByProviderOrderId("order_123")).thenReturn(Optional.of(transaction));
        when(gatewayRegistry.get(PaymentProvider.RAZORPAY)).thenReturn(paymentGateway);
        when(paymentGateway.verifyPaymentSignature(anyString(), anyString(), anyString())).thenReturn(false);
        when(paymentTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatThrownBy(() -> subscriptionService.verifyPayment("user1",
                new VerifyPaymentRequest("order_123", "pay_123", "bad_sig")))
                .isInstanceOf(PaymentException.class);

        assertThat(transaction.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should be idempotent when payment already captured")
    void verifyPayment_shouldBeIdempotent() {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setStatus(PaymentStatus.CAPTURED);

        when(paymentTransactionRepository.findByProviderOrderId("order_123")).thenReturn(Optional.of(transaction));

        subscriptionService.verifyPayment("user1", new VerifyPaymentRequest("order_123", "pay_123", "sig_123"));

        verify(gatewayRegistry, never()).get(any());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return subscription status with active subscription")
    void getSubscriptionStatus_withActiveSub() {
        Subscription sub = new Subscription();
        sub.setPlan(SubscriptionPlan.YEARLY);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setStartDate(Instant.now());
        sub.setEndDate(Instant.now().plus(365, ChronoUnit.DAYS));

        testUser.setFreeChatCount(3);

        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findByUser_IdAndStatus("user1", SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(sub));
        when(chatProperties.freeChatLimit()).thenReturn(5);

        SubscriptionStatusResponse status = subscriptionService.getSubscriptionStatus("user1");

        assertThat(status.hasActiveSubscription()).isTrue();
        assertThat(status.plan()).isEqualTo(SubscriptionPlan.YEARLY);
        assertThat(status.freePromptsUsed()).isEqualTo(3);
        assertThat(status.freePromptsLimit()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return no subscription status when none active")
    void getSubscriptionStatus_withNoSub() {
        testUser.setFreeChatCount(2);

        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findByUser_IdAndStatus("user1", SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(chatProperties.freeChatLimit()).thenReturn(5);

        SubscriptionStatusResponse status = subscriptionService.getSubscriptionStatus("user1");

        assertThat(status.hasActiveSubscription()).isFalse();
        assertThat(status.plan()).isNull();
    }

    @Test
    @DisplayName("Should activate lifetime subscription without end date")
    void verifyPayment_lifetimePlan_shouldHaveNoEndDate() {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUser(testUser);
        transaction.setPlan(SubscriptionPlan.LIFETIME);
        transaction.setProvider(PaymentProvider.RAZORPAY);
        transaction.setProviderOrderId("order_lt");
        transaction.setStatus(PaymentStatus.CREATED);

        var planConfig = new PaymentProperties.PlanConfig(999900L, null);

        when(paymentTransactionRepository.findByProviderOrderId("order_lt")).thenReturn(Optional.of(transaction));
        when(gatewayRegistry.get(PaymentProvider.RAZORPAY)).thenReturn(paymentGateway);
        when(paymentGateway.verifyPaymentSignature("order_lt", "pay_lt", "sig_lt")).thenReturn(true);
        when(paymentTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(subscriptionRepository.findByUser_IdAndStatus(any(), any())).thenReturn(Optional.empty());
        when(paymentProperties.plans()).thenReturn(Map.of("LIFETIME", planConfig));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> {
            Subscription saved = inv.getArgument(0);
            assertThat(saved.getEndDate()).isNull();
            assertThat(saved.getPlan()).isEqualTo(SubscriptionPlan.LIFETIME);
            return saved;
        });

        subscriptionService.verifyPayment("user1", new VerifyPaymentRequest("order_lt", "pay_lt", "sig_lt"));

        verify(subscriptionRepository).save(any(Subscription.class));
    }
}
