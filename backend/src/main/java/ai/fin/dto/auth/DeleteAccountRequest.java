package ai.fin.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for account deletion confirmation")
public record DeleteAccountRequest(

        @Schema(description = "Current account password, required to confirm identity")
        @NotBlank(message = "Password is required")
        String password
) {
}