package ai.fin.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user login")
public record LoginRequest(

        @Schema(description = "User's email address", example = "john@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        String email,

        @Schema(description = "Account password", example = "secret123")
        @NotBlank(message = "Password is required")
        String password
) {
}