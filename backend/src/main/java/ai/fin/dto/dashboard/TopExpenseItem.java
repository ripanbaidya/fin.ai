package ai.fin.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        name = "TopExpenseItem",
        description = "Represents a top expense transaction for the selected period"
)
public record TopExpenseItem(

        @Schema(description = "Unique identifier of the transaction")
        String id,

        @Schema(
                description = "Amount spent in the transaction",
                example = "1250.75"
        )
        BigDecimal amount,

        @Schema(
                description = "Category name associated with the expense (may be null if uncategorized)",
                example = "Food",
                nullable = true
        )
        String categoryName,

        @Schema(
                description = "Optional note attached to the transaction",
                example = "Dinner at restaurant"
        )
        String note,

        @Schema(
                description = "Date when the transaction occurred",
                example = "2026-03-14"
        )
        LocalDate date

) {
}