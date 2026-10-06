package ai.fin.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        name = "DailyTrendItem",
        description = "Represents daily income and expense totals for trend visualization"
)
public record DailyTrendItem(

        @Schema(
                description = "Date for which the financial data is aggregated",
                example = "2026-04-15"
        )
        LocalDate date,

        @Schema(
                description = "Total income recorded on the given date",
                example = "1200.00"
        )
        BigDecimal income,

        @Schema(
                description = "Total expense recorded on the given date",
                example = "850.50"
        )
        BigDecimal expense

) {
}