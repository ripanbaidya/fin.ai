package ai.fin.dto.payment;

import ai.fin.enums.SubscriptionPlan;
import ai.fin.enums.SubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Response containing the user's subscription status and usage details")
public record SubscriptionStatusResponse(

        @Schema(
                description = "Whether the user currently has an active subscription",
                example = "true"
        )
        boolean hasActiveSubscription,

        @Schema(
                description = "Current subscription plan; null if no plan is associated",
                example = "MONTHLY",
                nullable = true
        )
        SubscriptionPlan plan,

        @Schema(
                description = "Current subscription status",
                example = "ACTIVE",
                nullable = true
        )
        SubscriptionStatus status,

        @Schema(
                description = "Subscription start date and time in ISO 8601 format",
                example = "2026-10-01T00:00:00Z",
                type = "string",
                format = "date-time",
                nullable = true
        )
        Instant startDate,

        @Schema(
                description = "Subscription end date and time in ISO 8601 format",
                example = "2026-10-31T23:59:59Z",
                type = "string",
                format = "date-time",
                nullable = true
        )
        Instant endDate,

        @Schema(
                description = "Number of free prompts used by the user",
                example = "5",
                minimum = "0"
        )
        int freePromptsUsed,

        @Schema(
                description = "Maximum number of free prompts allowed",
                example = "10",
                minimum = "0"
        )
        int freePromptsLimit
) {
}