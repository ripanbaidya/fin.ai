package ai.fin.controller;

import ai.fin.dto.payment.SubscriptionStatusResponse;
import ai.fin.security.UserPrincipal;
import ai.fin.service.SubscriptionService;
import ai.fin.shared.api.ApiSuccessResponse;
import ai.fin.shared.api.SuccessCode;
import ai.fin.shared.api.factory.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subscriptions", description = "APIs for subscription status")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Get current subscription status")
    @GetMapping("/status")
    public ResponseEntity<ApiSuccessResponse<SubscriptionStatusResponse>> getStatus(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var status = subscriptionService.getSubscriptionStatus(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Subscription status fetched successfully",
                status
        );
    }
}
