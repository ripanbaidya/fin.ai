package ai.fin.dto.paymentmode;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "PaymentModeResponse",
        description = "Payment mode returned by the API"
)
public record PaymentModeResponse(

        @Schema(description = "Unique identifier of the payment mode")
        String id,

        @Schema(description = "Name of the payment mode", example = "UPI")
        String name,

        @Schema(description = "Indicates whether this payment mode is a system default", example = "true")
        boolean isDefault

) {
}