package ai.fin.dto.transaction;

import ai.fin.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        name = "TransactionResponse",
        description = "Response payload representing a transaction"
)
public record TransactionResponse(

        @Schema(
                description = "Unique identifier of the transaction",
                example = "2d9a93df-8d2e-4b32-9f7e-1d7c7a12c567"
        )
        String id,

        @Schema(
                description = "Transaction amount",
                example = "1250.50"
        )
        BigDecimal amount,

        @Schema(
                description = "Type of transaction",
                example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE"}
        )
        TxnType type,

        @Schema(
                description = "Date when the transaction occurred",
                example = "2026-03-16",
                type = "string",
                format = "date"
        )
        LocalDate date,

        @Schema(
                description = "Optional note describing the transaction",
                example = "Dinner with friends"
        )
        String note,

        @Schema(
                description = "Category ID associated with the transaction",
                example = "9c5c0f4c-9a3d-4b22-9a11-8f8c9b0c1234"
        )
        String categoryId,

        @Schema(
                description = "Name of the category associated with the transaction",
                example = "Food"
        )
        String categoryName,

        @Schema(
                description = "Payment mode ID used for the transaction",
                example = "7b1fce20-8e9a-4a45-9b73-3a2dfecb8a21"
        )
        String paymentModeId,

        @Schema(
                description = "Name of the payment mode used for the transaction",
                example = "UPI"
        )
        String paymentModeName,

        @Schema(
                description = "Identifier of the vector embedding associated with the transaction",
                nullable = true
        )
        String embeddingId
) {
}