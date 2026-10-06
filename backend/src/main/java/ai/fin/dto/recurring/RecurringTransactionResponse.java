package ai.fin.dto.recurring;

import ai.fin.enums.RecurringFrequency;
import ai.fin.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(
        name = "RecurringTransactionResponse",
        description = "Represents a recurring transaction configuration"
)
public record RecurringTransactionResponse(

        @Schema(description = "Unique identifier of the recurring transaction")
        String id,

        @Schema(
                description = "Title of the recurring transaction",
                example = "Wifi Bill"
        )
        String title,

        @Schema(
                description = "Amount of the recurring transaction",
                example = "500.00"
        )
        BigDecimal amount,

        @Schema(
                description = "Type of transaction",
                example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE"}
        )
        TxnType type,

        @Schema(
                description = "Frequency at which the transaction recurs",
                example = "MONTHLY",
                allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "YEARLY"}
        )
        RecurringFrequency frequency,

        @Schema(
                description = "Date when the recurring transaction starts",
                example = "2026-04-01"
        )
        LocalDate startDate,

        @Schema(
                description = "Optional end date of the recurring transaction",
                example = "2027-04-01"
        )
        LocalDate endDate,

        @Schema(
                description = "Next scheduled execution date for this recurring transaction",
                example = "2026-05-01"
        )
        LocalDate nextExecutionDate,

        @Schema(
                description = "Indicates whether the recurring transaction is currently active",
                example = "true"
        )
        boolean isActive,

        @Schema(
                description = "Optional note associated with the recurring transaction",
                example = "Monthly wifi bill"
        )
        String note,

        @Schema(
                description = "Name of the category associated with the recurring transaction",
                example = "Utilities"
        )
        String categoryName,

        @Schema(
                description = "Name of the payment mode used for the recurring transaction",
                example = "UPI"
        )
        String paymentModeName,

        @Schema(
                description = "Timestamp when the recurring transaction was created",
                example = "2026-03-16T10:30:45Z"
        )
        String createdAt

) {
}