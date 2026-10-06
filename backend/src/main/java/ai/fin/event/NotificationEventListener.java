package ai.fin.event;

import ai.fin.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final FcmService fcmService;

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onNotificationEvent(NotificationEvent event) {
        try {
            log.debug("Handling NotificationEvent for user {}: {}", event.userId(), event.title());
            fcmService.sendPushToUser(
                    event.userId(),
                    event.title(),
                    event.message(),
                    event.data()
            );
        } catch (Exception e) {
            log.error("Failed to process FCM push for NotificationEvent: {}", e.getMessage(), e);
        }
    }
}
