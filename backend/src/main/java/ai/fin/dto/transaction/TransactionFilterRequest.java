package ai.fin.dto.transaction;

import ai.fin.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        name = "TransactionFilterRequest",
        description = "Request object used to filter transactions by type, category, and date range"
)
public record TransactionFilterRequest(

        @Schema(
                description = "Transaction type to filter results",
                example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE"},
                nullable = true
        )
        TxnType type,

        @Schema(
                description = "Category ID used to filter transactions",
                example = "9c5c0f4c-9a3d-4b22-9a11-8f8c9b0c1234",
                nullable = true
        )
        String categoryId,

        @Schema(
                description = "Start date of the filtering range (inclusive)",
                example = "2026-01-01",
                type = "string",
                format = "date",
                nullable = true
        )
        LocalDate dateFrom,

        @Schema(
                description = "End date of the filtering range (inclusive)",
                example = "2026-01-31",
                type = "string",
                format = "date",
                nullable = true
        )
        LocalDate dateTo

) {
        public static TransactionFilterRequest from(TxnType type, String categoryId, LocalDate dateFrom, LocalDate dateTo) {
                return new TransactionFilterRequest(type, categoryId, dateFrom, dateTo);
        }
}