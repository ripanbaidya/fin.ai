package ai.fin.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.YearMonth;

@Schema(
        name = "BudgetResponse",
        description = "Response payload representing a monthly budget for a specific category"
)
public record BudgetResponse(

        @Schema(description = "Unique identifier of the budget record")
        String id,

        @Schema(description = "Display name of the category associated with this budget")
        String categoryName,

        @Schema(description = "Unique identifier of the category")
        String categoryId,

        @Schema(description = "Budget month in format yyyy-MM", example = "2026-04", type = "string")
        YearMonth month,

        @Schema(description = "Maximum spending limit allowed for this category during the specified month")
        BigDecimal limitAmount,

        @Schema(description = "Percentage threshold that triggers a budget alert when spending approaches the limit")
        int alertThreshold

) {
}