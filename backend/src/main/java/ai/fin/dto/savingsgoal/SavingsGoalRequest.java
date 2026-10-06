package ai.fin.dto.savingsgoal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        name = "SavingsGoalRequest",
        description = "Request payload to create a new savings goal"
)
public record SavingsGoalRequest(

        @Schema(
                description = "Title of the savings goal",
                example = "Buy MacBook Pro",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Savings goal title must not be blank")
        @Size(
                max = 100,
                message = "Savings goal title must not exceed 100 characters"
        )
        String title,

        @Schema(
                description = "Target amount to save for this goal",
                example = "150000.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Target amount is required")
        @DecimalMin(
                value = "0.01",
                inclusive = true,
                message = "Target amount must be greater than 0"
        )
        @Digits(
                integer = 10,
                fraction = 2,
                message = "Target amount must have at most 2 decimal places"
        )
        BigDecimal targetAmount,

        @Schema(
                description = "Deadline by which the savings goal should be achieved",
                example = "2026-12-31",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Deadline is required")
        @Future(message = "Deadline must be a future date")
        LocalDate deadline,

        @Schema(
                description = "Optional note providing additional context about the savings goal",
                example = "Saving monthly for a new laptop",
                maxLength = 500
        )
        @Size(
                max = 500,
                message = "Note must not exceed 500 characters"
        )
        String note

) {
}