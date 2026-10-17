package ai.fin.dto.payment;

import ai.fin.enums.SubscriptionPlan;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Subscription plan details, including pricing and duration")
public record PlanResponse(

        @Schema(
                description = "Subscription plan identifier",
                example = "MONTHLY"
        )
        SubscriptionPlan plan,

        @Schema(
                description = "Plan price in the smallest currency unit (paise for INR)",
                example = "49900"
        )
        long priceInPaise,

        @Schema(
                description = "Subscription duration in days; null if the duration is not specified",
                example = "30",
                nullable = true
        )
        Integer durationDays
) {
}