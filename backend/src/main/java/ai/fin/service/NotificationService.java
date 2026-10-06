package ai.fin.service;

import ai.fin.dto.notification.NotificationResponse;
import ai.fin.dto.notification.UnreadNotificationCountResponse;
import ai.fin.enums.NotificationType;
import ai.fin.shared.api.PaginatedData;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    PaginatedData<NotificationResponse> getNotifications(String userId, Boolean unreadOnly, NotificationType type, Pageable pageable);

    UnreadNotificationCountResponse getUnreadCount(String userId);

    NotificationResponse markAsRead(String userId, String notificationId);

    void markAllAsRead(String userId);

    void deleteNotification(String userId, String notificationId);

    void deleteAllNotifications(String userId, boolean readOnly);

    NotificationResponse sendNotification(String userId, String title, String message, NotificationType type, Map<String, String> data);

    void sendBatchNotification(List<String> userIds, String title, String message, NotificationType type, Map<String, String> data);
}
