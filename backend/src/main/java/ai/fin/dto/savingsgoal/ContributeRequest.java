package ai.fin.dto.savingsgoal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(
        name = "ContributeRequest",
        description = "Request payload to contribute an amount toward a savings goal"
)
public record ContributeRequest(

        @Schema(
                description = "Amount to contribute to the savings goal",
                example = "500.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Contribution amount is required")
        @DecimalMin(
                message = "Amount must be greater than zero",
                value = "0.01"
        )
        @Digits(
                integer = 10,
                fraction = 2,
                message = "Amount must have up to 10 integer digits and 2 decimal places"
        )
        BigDecimal amount

) {
}