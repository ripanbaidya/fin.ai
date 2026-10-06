package ai.fin.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "SendOtpRequest",
        description = "Request payload for sending an OTP to a user's email"
)
public record SendOtpRequest(

        @Schema(
                description = "Email address where the OTP will be sent",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Must be a valid email address")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email

) {
}