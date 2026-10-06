package ai.fin.dto.devices;

import ai.fin.enums.DevicePlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        name = "RegisterDeviceTokenRequest",
        description = "Request payload for registering a device token for push notifications"
)
public record RegisterDeviceTokenRequest(

        @Schema(
                description = "Device push notification token",
                example = "fcm_device_token_abc123xyz",
                maxLength = 512,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Device token must not be blank")
        @Size(max = 512, message = "Device token must not exceed 512 characters")
        String token,

        @Schema(
                description = "Platform of the device associated with the token",
                example = "WEB",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Device platform must not be null")
        DevicePlatform platform
) {
}
