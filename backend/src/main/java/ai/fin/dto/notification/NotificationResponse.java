package ai.fin.dto.notification;

import ai.fin.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
@Schema(name = "NotificationResponse", description = "Notification response representation")
public record NotificationResponse(
        @Schema(description = "Notification ID", example = "43412a68-d25a-42fe-842b-df2d3ed08b43")
        String id,

        @Schema(description = "Notification title", example = "Budget Exceeded")
        String title,

        @Schema(description = "Notification message", example = "You have exceeded your monthly dining budget.")
        String message,

        @Schema(description = "Notification type", example = "BUDGET_EXCEEDED")
        NotificationType type,

        @Schema(description = "Whether the notification has been read", example = "false")
        boolean isRead,

        @Schema(description = "Timestamp when the notification was marked as read", example = "2026-10-05T10:15:30Z")
        Instant readAt,

        @Schema(description = "Additional key-value metadata payload")
        Map<String, String> data,

        @Schema(description = "Creation timestamp", example = "2026-10-05T10:00:00Z")
        Instant createdAt,

        @Schema(description = "Last update timestamp", example = "2026-10-05T10:00:00Z")
        Instant updatedAt
) {
}
