package ai.fin.dto.recurring;

import ai.fin.enums.RecurringFrequency;
import ai.fin.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        name = "RecurringTransactionRequest",
        description = "Request payload to create a recurring transaction"
)
public record RecurringTransactionRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        @Schema(
                description = "Title of the recurring transaction",
                example = "Wifi Bill",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits and 2 decimal places")
        @Schema(
                description = "Transaction amount",
                example = "500.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal amount,

        @NotNull(message = "Transaction type is required")
        @Schema(
                description = "Type of transaction",
                allowableValues = {"INCOME", "EXPENSE"},
                example = "EXPENSE",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TxnType type,

        @NotNull(message = "Recurring frequency is required")
        @Schema(
                description = "Frequency of the recurring transaction",
                allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "YEARLY"},
                example = "MONTHLY",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        RecurringFrequency frequency,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date must be today or in the future")
        @Schema(
                description = "Date when the recurring transaction starts",
                example = "2026-04-01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDate startDate,

        @Future(message = "End date must be in the future")
        @Schema(
                description = "Optional end date of the recurring transaction. Must be after the start date",
                example = "2027-04-01"
        )
        LocalDate endDate,

        @Size(max = 255, message = "Note must not exceed 255 characters")
        @Schema(
                description = "Optional note for the transaction",
                example = "Monthly wifi bill"
        )
        String note,

        @NotNull(message = "Category ID is required")
        @Schema(
                description = "Category ID associated with the transaction",
                example = "c7d8e9f1-6b2a-4c5d-8f3e-2a1b0c9d8e7f",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String categoryId,

        @NotNull(message = "Payment mode ID is required")
        @Schema(
                description = "Payment mode ID used for the transaction",
                example = "b3c4d5e6-7f8a-4b2c-9d1e-0a2b3c4d5e6f",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String paymentModeId

) {
}