package ai.fin.dto.recurring;

import ai.fin.enums.RecurringFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        name = "UpdateRecurringTransactionRequest",
        description = "Request payload to update an existing recurring transaction"
)
public record UpdateRecurringTransactionRequest(

        @Schema(
                description = "Updated title of the recurring transaction",
                example = "Updated Wifi Bill",
                maxLength = 100
        )
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,

        @Schema(
                description = "Updated transaction amount",
                example = "650.00"
        )
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits and 2 decimal places")
        BigDecimal amount,

        @Schema(
                description = "Updated recurring frequency",
                allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "YEARLY"},
                example = "MONTHLY"
        )
        RecurringFrequency frequency,

        @Schema(
                description = "Updated end date for the recurring transaction",
                example = "2027-04-01"
        )
        @Future(message = "End date must be in the future")
        LocalDate endDate,

        @Schema(
                description = "Updated note for the transaction",
                example = "Updated monthly wifi bill",
                maxLength = 255
        )
        @Size(max = 255, message = "Note must not exceed 255 characters")
        String note,

        @Schema(
                description = "Updated category ID",
                example = "c7d8e9f1-6b2a-4c5d-8f3e-2a1b0c9d8e7f"
        )
        String categoryId,

        @Schema(
                description = "Updated payment mode ID",
                example = "b3c4d5e6-7f8a-4b2c-9d1e-0a2b3c4d5e6f"
        )
        String paymentModeId

) {
}