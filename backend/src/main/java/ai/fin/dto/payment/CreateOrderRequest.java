package ai.fin.dto.payment;

import ai.fin.enums.SubscriptionPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload for creating a payment order")
public record CreateOrderRequest(

        @NotNull
        @Schema(
                description = "Subscription plan for which the payment order will be created",
                example = "MONTHLY",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        SubscriptionPlan plan
) {
}