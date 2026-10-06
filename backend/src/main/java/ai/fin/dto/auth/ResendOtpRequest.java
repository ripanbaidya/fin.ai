package ai.fin.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "ResendOtpRequest",
        description = "Request payload for resending a verification OTP to a user's email"
)
public record ResendOtpRequest(

        @Schema(
                description = "Email address where the verification OTP will be resent",
                example = "john@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Must be a valid email address")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email

) {
}
