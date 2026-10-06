package ai.fin.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Schema(name = "BudgetRequest", description = "Request payload for creating or updating a monthly category budget")
public record BudgetRequest(

        @Schema(
                description = "Unique identifier of the category for which the budget is configured",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Category ID is required")
        String categoryId,

        @Schema(
                description = "Budget month in format yyyy-MM",
                example = "2026-04",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Month is required and must follow format yyyy-MM")
        YearMonth month,

        @Schema(
                description = "Maximum amount allowed to spend for the category during the specified month",
                example = "5000.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Budget limit amount is required")
        @DecimalMin(value = "1.00", message = "Budget limit must be at least 1.00")
        @Digits(
                integer = 10,
                fraction = 2,
                message = "Amount must have up to 10 integer digits and 2 decimal places"
        )
        BigDecimal limitAmount,

        @Schema(
                description = "Percentage threshold when a budget alert should be triggered",
                example = "80", defaultValue = "80", minimum = "1", maximum = "100"
        )
        @Min(value = 1, message = "Alert threshold must be at least 1%")
        @Max(value = 100, message = "Alert threshold cannot exceed 100%")
        int alertThreshold

) {

    public BudgetRequest {
        if (alertThreshold == 0) {
            alertThreshold = 80;
        }
    }
}