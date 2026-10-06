package ai.fin.dto.devices;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "UnregisterDeviceTokenRequest",
        description = "Request payload for unregistering a device token"
)
public record UnregisterDeviceTokenRequest(

        @Schema(
                description = "Device push notification token to unregister",
                example = "fcm_device_token_abc123xyz",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Device token must not be blank")
        String token

) {
}