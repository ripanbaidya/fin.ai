package ai.fin.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "UpdateProfileRequest",
        description = "Request to update user profile"
)
public record UpdateProfileRequest(

        @NotBlank(message = "Full name cannot be blank")
        @Schema(description = "Full name of user", example = "John Doe")
        String fullName

) {
}
