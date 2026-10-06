package ai.fin.service.impl;

import ai.fin.dto.notification.NotificationResponse;
import ai.fin.dto.notification.UnreadNotificationCountResponse;
import ai.fin.entities.Notification;
import ai.fin.entities.User;
import ai.fin.enums.NotificationType;
import ai.fin.event.NotificationEvent;
import ai.fin.mapper.NotificationMapper;
import ai.fin.repository.NotificationRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.NotificationService;
import ai.fin.shared.api.PaginatedData;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<NotificationResponse> getNotifications(String userId, Boolean unreadOnly, NotificationType type, Pageable pageable) {
        Page<Notification> page;

        if (Boolean.TRUE.equals(unreadOnly)) {
            if (type != null) {
                page = notificationRepository.findByUser_IdAndIsReadAndType(userId, false, type, pageable);
            } else {
                page = notificationRepository.findByUser_IdAndIsRead(userId, false, pageable);
            }
        } else {
            if (type != null) {
                page = notificationRepository.findByUser_IdAndType(userId, type, pageable);
            } else {
                page = notificationRepository.findByUser_Id(userId, pageable);
            }
        }

        Page<NotificationResponse> mapped = page.map(NotificationMapper::toResponse);
        return PaginatedData.from(mapped);
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse getUnreadCount(String userId) {
        long count = notificationRepository.countByUser_IdAndIsReadFalse(userId);
        return new UnreadNotificationCountResponse(count);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(String userId, String notificationId) {
        Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(Instant.now());
            notificationRepository.saveAndFlush(notification);
            log.debug("Marked notification {} as read for user {}", notificationId, userId);
        }

        return NotificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        int updated = notificationRepository.markAllAsRead(userId, Instant.now());
        log.info("Marked {} unread notifications as read for user {}", updated, userId);
    }

    @Override
    @Transactional
    public void deleteNotification(String userId, String notificationId) {
        int deleted = notificationRepository.deleteByIdAndUserId(notificationId, userId);
        if (deleted == 0) {
            throw new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        log.debug("Deleted notification {} for user {}", notificationId, userId);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(String userId, boolean readOnly) {
        if (readOnly) {
            int deleted = notificationRepository.deleteAllReadByUserId(userId);
            log.info("Deleted {} read notifications for user {}", deleted, userId);
        } else {
            int deleted = notificationRepository.deleteAllByUserId(userId);
            log.info("Deleted all ({}) notifications for user {}", deleted, userId);
        }
    }

    @Override
    @Transactional
    public NotificationResponse sendNotification(String userId, String title, String message, NotificationType type, Map<String, String> data) {
        User user = userRepository.getReferenceById(userId);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type != null ? type : NotificationType.GENERAL);
        notification.setRead(false);
        notification.setData(data != null ? new HashMap<>(data) : new HashMap<>());

        Notification saved = notificationRepository.saveAndFlush(notification);

        // Publish event for asynchronous push dispatch
        eventPublisher.publishEvent(new NotificationEvent(
                userId,
                title,
                message,
                notification.getType(),
                notification.getData()
        ));

        log.info("Created notification [{}] of type {} for user {}", saved.getId(), saved.getType(), userId);
        return NotificationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void sendBatchNotification(List<String> userIds, String title, String message, NotificationType type, Map<String, String> data) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        List<Notification> notifications = new ArrayList<>();
        Map<String, String> payload = data != null ? new HashMap<>(data) : new HashMap<>();
        NotificationType effectiveType = type != null ? type : NotificationType.GENERAL;

        for (String userId : userIds) {
            User user = userRepository.getReferenceById(userId);
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(effectiveType);
            notification.setRead(false);
            notification.setData(payload);
            notifications.add(notification);
        }

        notificationRepository.saveAllAndFlush(notifications);

        // Publish events for push notification
        for (String userId : userIds) {
            eventPublisher.publishEvent(new NotificationEvent(
                    userId,
                    title,
                    message,
                    effectiveType,
                    payload
            ));
        }

        log.info("Dispatched batch notification '{}' to {} users", title, userIds.size());
    }
}
