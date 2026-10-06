package ai.fin.dto.notification;

import ai.fin.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(name = "SendNotificationRequest", description = "Payload for sending a notification")
public record SendNotificationRequest(
        @Schema(description = "Notification title", example = "Budget Exceeded", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Notification title must not be blank")
        String title,

        @Schema(description = "Notification message", example = "You have exceeded your monthly dining budget.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Notification message must not be blank")
        String message,

        @Schema(description = "Notification type", example = "BUDGET_EXCEEDED", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Notification type must not be null")
        NotificationType type,

        @Schema(description = "Additional key-value metadata payload")
        Map<String, String> data
) {
}
