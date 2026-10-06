package ai.fin.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "DashboardResponse",
        description = "Aggregated dashboard data for a specific month"
)
public record DashboardResponse(

        @Schema(
                description = "Month for which dashboard data is generated (format: yyyy-MM)",
                example = "2025-03"
        )
        String month,

        @Schema(description = "Summary metrics including total income, expenses, and balance")
        DashboardSummary summary,

        @Schema(description = "Expense breakdown grouped by category")
        List<CategoryBreakdownResponse> expenseByCategory,

        @Schema(description = "Income breakdown grouped by category")
        List<CategoryBreakdownResponse> incomeByCategory,

        @Schema(description = "Daily financial trend showing income and expenses over the month")
        List<DailyTrendItem> dailyTrend,

        @Schema(description = "List of highest expenses recorded during the month")
        List<TopExpenseItem> topExpenses

) {
}