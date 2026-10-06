package ai.fin.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public record UserRegisterRequest(

        @Schema(description = "User's full name", example = "John Doe")
        @NotBlank(message = "Full name is required")
        String fullName,

        @Schema(description = "User's email address", example = "john@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        String email,

        @Schema(description = "User's password, at least 8 characters", example = "secret123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}