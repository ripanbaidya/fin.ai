package ai.fin.dto.paymentmode;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "UpdatePaymentModeRequest",
        description = "Request payload for updating a payment mode"
)
public record UpdatePaymentModeRequest(

        @Schema(
                description = "Updated name of the payment mode",
                example = "Credit Card",
                minLength = 1,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Payment mode name must not be blank")
        @Size(min = 1, max = 100, message = "Payment mode name must be between 1 and 100 characters")
        String name

) {
}