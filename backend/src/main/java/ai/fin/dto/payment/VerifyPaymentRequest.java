package ai.fin.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for verifying a Razorpay payment")
public record VerifyPaymentRequest(

        @NotBlank
        @Schema(
                description = "Razorpay order ID associated with the payment",
                example = "order_NX123456789",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String razorpayOrderId,

        @NotBlank
        @Schema(
                description = "Razorpay payment ID returned after checkout",
                example = "pay_NX987654321",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String razorpayPaymentId,

        @NotBlank
        @Schema(
                description = "Razorpay signature used to verify payment authenticity on the server",
                example = "a1b2c3d4e5f67890abcdef1234567890",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String razorpaySignature
) {
}