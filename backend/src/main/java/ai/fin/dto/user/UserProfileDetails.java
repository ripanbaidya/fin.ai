package ai.fin.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "UserResponse",
        description = "User account response"
)
public record UserProfileDetails(

        @Schema(description = "Unique user ID (UUID)", example = "a1b2c3d4-e5f6-...")
        String id,

        @Schema(description = "Full name of user", example = "John Doe")
        String fullName,

        @Schema(description = "Email of the user", example = "user@example.com")
        String email,

        @Schema(description = "Current account status", example = "ACTIVE")
        String status
) {
}
