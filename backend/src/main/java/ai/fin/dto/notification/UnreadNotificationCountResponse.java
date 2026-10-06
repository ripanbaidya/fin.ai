package ai.fin.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UnreadNotificationCountResponse", description = "Unread notification count payload")
public record UnreadNotificationCountResponse(

        @Schema(description = "Number of unread notifications", example = "5")
        long unreadCount
) {
}
