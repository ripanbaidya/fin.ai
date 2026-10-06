package ai.fin.dto.recurring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Schema(
        name = "ForecastSummaryResponse",
        description = "Summary of projected recurring transactions for the forecast period"
)
public record ForecastSummaryResponse(

        @Schema(
                description = "Number of days included in the forecast window",
                example = "30"
        )
        int forecastDays,

        @Schema(
                description = "Total projected income during the forecast period",
                example = "25000.00"
        )
        BigDecimal projectedIncome,

        @Schema(
                description = "Total projected expenses during the forecast period",
                example = "18000.00"
        )
        BigDecimal projectedExpense,

        @Schema(
                description = "Projected net balance (income minus expenses) for the forecast period",
                example = "7000.00"
        )
        BigDecimal projectedNetBalance,

        @Schema(
                description = "List of projected recurring transaction entries included in the forecast"
        )
        List<ForecastEntryResponse> entries

) {
}