package ai.fin.controller;

import ai.fin.dto.payment.*;
import ai.fin.security.UserPrincipal;
import ai.fin.service.SubscriptionService;
import ai.fin.shared.api.ApiSuccessResponse;
import ai.fin.shared.api.SuccessCode;
import ai.fin.shared.api.factory.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payments", description = "APIs for subscription payments and order management")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "List available subscription plans")
    @GetMapping("/plans")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiSuccessResponse<List<PlanResponse>>> getPlans() {
        var plans = subscriptionService.getPlans();
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Plans fetched successfully",
                plans
        );
    }

    @Operation(summary = "Create a payment order for a subscription plan")
    @PostMapping("/orders")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiSuccessResponse<CreateOrderResponse>> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        var response = subscriptionService.createOrder(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "Order created successfully",
                response
        );
    }

    @Operation(summary = "Verify payment signature after checkout")
    @PostMapping("/verify")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiSuccessResponse<Void>> verifyPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VerifyPaymentRequest request
    ) {
        subscriptionService.verifyPayment(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Payment verified successfully",
                null
        );
    }

    @Operation(summary = "Razorpay webhook endpoint")
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature
    ) {
        subscriptionService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get payment transaction history")
    @GetMapping("/history")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiSuccessResponse<List<PaymentHistoryResponse>>> getHistory(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var history = subscriptionService.getPaymentHistory(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Payment history fetched successfully",
                history
        );
    }
}
