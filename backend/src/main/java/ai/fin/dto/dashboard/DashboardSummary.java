package ai.fin.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "DashboardSummary",
        description = "Summary of financial metrics for the selected dashboard period"
)
public record DashboardSummary(

        @Schema(
                description = "Total income recorded for the selected period",
                example = "15000.00"
        )
        BigDecimal totalIncome,

        @Schema(
                description = "Total expenses recorded for the selected period",
                example = "8200.50"
        )
        BigDecimal totalExpense,

        @Schema(
                description = "Net balance calculated as total income minus total expenses",
                example = "6799.50"
        )
        BigDecimal netBalance

) {
}