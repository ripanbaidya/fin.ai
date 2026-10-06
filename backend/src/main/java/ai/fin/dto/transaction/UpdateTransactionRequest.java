package ai.fin.dto.transaction;

import ai.fin.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        name = "UpdateTransactionRequest",
        description = "Request payload used to update an existing transaction"
)
public record UpdateTransactionRequest(

        @Schema(
                description = "Updated transaction amount",
                example = "950.75",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
        BigDecimal amount,

        @Schema(
                description = "Updated transaction type",
                example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE"},
                nullable = true
        )
        TxnType type,

        @Schema(
                description = "Updated transaction date",
                example = "2026-03-16",
                type = "string",
                format = "date",
                nullable = true
        )
        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate date,

        @Schema(
                description = "Optional note describing the transaction",
                example = "Dinner with colleagues",
                maxLength = 1000,
                nullable = true
        )
        @Size(max = 1000, message = "Note must not exceed 1000 characters")
        String note,

        @Schema(
                description = "Updated category ID associated with the transaction",
                example = "9c5c0f4c-9a3d-4b22-9a11-8f8c9b0c1234",
                nullable = true
        )
        String categoryId,

        @Schema(
                description = "Updated payment mode ID used for the transaction",
                example = "7b1fce20-8e9a-4a45-9b73-3a2dfecb8a21",
                nullable = true
        )
        String paymentModeId

) {
}