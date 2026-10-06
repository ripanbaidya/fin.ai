package ai.fin.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to verify a registered email address")
public record VerifyEmailRequest(

        @Schema(description = "Email address to verify", example = "john@example.com")
        @NotBlank(message = "Email is required")
        @Email
        String email,

        @Schema(description = "OTP received via email")
        @NotBlank(message = "OTP is required")
        String otp
) {
}