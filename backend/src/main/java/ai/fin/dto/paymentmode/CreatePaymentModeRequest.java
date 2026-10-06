package ai.fin.dto.paymentmode;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "CreatePaymentModeRequest",
        description = "Request payload for creating a new payment mode"
)
public record CreatePaymentModeRequest(

        @Schema(
                description = "Name of the payment mode",
                example = "UPI",
                minLength = 1,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Payment mode name must not be blank")
        @Size(min = 1, max = 100, message = "Payment mode name must be between 1 and 100 characters")
        String name

) {
}