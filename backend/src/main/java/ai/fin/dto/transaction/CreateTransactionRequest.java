package ai.fin.dto.transaction;

import ai.fin.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        name = "CreateTransactionRequest",
        description = "Request payload to create a new transaction"
)
public record CreateTransactionRequest(

        @Schema(
                description = "Transaction amount",
                example = "1250.50",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
        BigDecimal amount,

        @Schema(
                description = "Type of transaction",
                example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Transaction type is required")
        TxnType type,

        @Schema(
                description = "Date of the transaction",
                example = "2026-04-01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Transaction date is required")
        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate date,

        @Schema(
                description = "Optional note describing the transaction",
                example = "Dinner with friends",
                maxLength = 1000
        )
        @Size(
                max = 1000,
                message = "Note must not exceed 1000 characters"
        )
        String note,

        @Schema(
                description = "Category ID associated with the transaction",
                example = "9c5c0f4c-9a3d-4b22-9a11-8f8c9b0c1234",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Category ID is required")
        String categoryId,

        @Schema(
                description = "Payment mode ID used for the transaction",
                example = "2d9a93df-8d2e-4b32-9f7e-1d7c7a12c567",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Payment mode ID is required")
        String paymentModeId

) {
}