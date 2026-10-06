package ai.fin.dto.recurring;

import ai.fin.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(
        name = "ForecastEntryResponse",
        description = "Represents a projected recurring transaction used for financial forecasting"
)
public record ForecastEntryResponse(

        @Schema(
                description = "Date when the recurring transaction is expected to occur",
                example = "2026-04-01"
        )
        LocalDate projectedDate,

        @Schema(
                description = "Title or description of the recurring transaction",
                example = "Monthly Rent"
        )
        String title,

        @Schema(
                description = "Amount of the recurring transaction",
                example = "12000.00"
        )
        BigDecimal amount,

        @Schema(
                description = "Type of transaction",
                example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE"}
        )
        TxnType type,

        @Schema(
                description = "Category associated with the recurring transaction",
                example = "Housing"
        )
        String categoryName

) {
}