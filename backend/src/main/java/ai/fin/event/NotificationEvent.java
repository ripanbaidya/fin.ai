package ai.fin.event;

import ai.fin.enums.NotificationType;

import java.util.Map;

public record NotificationEvent(
        String userId,
        String title,
        String message,
        NotificationType type,
        Map<String, String> data
) {
}
