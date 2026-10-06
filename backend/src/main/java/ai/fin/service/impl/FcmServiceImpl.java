package ai.fin.service.impl;

import ai.fin.service.DeviceTokenService;
import ai.fin.service.FcmService;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    private static final int FCM_MAX_BATCH_SIZE = 500;

    private final DeviceTokenService deviceTokenService;

    @Autowired(required = false)
    private FirebaseMessaging firebaseMessaging;

    @Override
    public void sendPushToUser(String userId, String title, String body, Map<String, String> data) {
        if (userId == null) {
            return;
        }

        List<String> tokens = deviceTokenService.getActiveTokens(userId);
        if (tokens.isEmpty()) {
            log.debug("No active device tokens found for user: {}", userId);
            return;
        }

        sendPushToTokens(tokens, title, body, data);
    }

    @Override
    public void sendPushToTokens(List<String> tokens, String title, String body, Map<String, String> data) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        if (firebaseMessaging == null) {
            log.debug("FirebaseMessaging is not initialized. Skipping push notification dispatch.");
            return;
        }

        // Deduplicate tokens
        List<String> uniqueTokens = tokens.stream().distinct().filter(t -> t != null && !t.isBlank()).toList();
        if (uniqueTokens.isEmpty()) {
            return;
        }

        // Batch send in chunks of up to 500 tokens (FCM multicast limit)
        for (int i = 0; i < uniqueTokens.size(); i += FCM_MAX_BATCH_SIZE) {
            List<String> batchTokens = uniqueTokens.subList(i, Math.min(i + FCM_MAX_BATCH_SIZE, uniqueTokens.size()));
            sendBatch(batchTokens, title, body, data);
        }
    }

    private void sendBatch(List<String> tokens, String title, String body, Map<String, String> data) {
        try {
            com.google.firebase.messaging.Notification fcmNotification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(fcmNotification)
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(WebpushNotification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .setIcon("/favicon.ico")
                                    .build())
                            .build());

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            MulticastMessage multicastMessage = messageBuilder.build();
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(multicastMessage);

            log.info("FCM batch dispatched: {} successful, {} failed out of {} total tokens",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), tokens.size());

            if (batchResponse.getFailureCount() > 0) {
                handleBatchFailures(tokens, batchResponse);
            }
        } catch (FirebaseMessagingException e) {
            log.error("FCM Multicast messaging error: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during FCM push dispatch: {}", e.getMessage(), e);
        }
    }

    private void handleBatchFailures(List<String> tokens, BatchResponse batchResponse) {
        List<String> invalidTokens = new ArrayList<>();
        List<SendResponse> responses = batchResponse.getResponses();

        for (int i = 0; i < responses.size(); i++) {
            SendResponse response = responses.get(i);
            if (!response.isSuccessful()) {
                String token = tokens.get(i);
                FirebaseMessagingException exception = response.getException();
                MessagingErrorCode errorCode = exception != null ? exception.getMessagingErrorCode() : null;

                log.warn("FCM push delivery failed for token [{}]: code={}, message={}",
                        maskToken(token), errorCode, exception != null ? exception.getMessage() : "unknown");

                // Invalidate dead or unregistered tokens automatically
                if (errorCode == MessagingErrorCode.UNREGISTERED
                        || errorCode == MessagingErrorCode.INVALID_ARGUMENT
                        || errorCode == MessagingErrorCode.SENDER_ID_MISMATCH) {
                    invalidTokens.add(token);
                }
            }
        }

        if (!invalidTokens.isEmpty()) {
            deviceTokenService.removeInvalidTokens(invalidTokens);
        }
    }

    private String maskToken(String token) {
        if (token == null || token.length() <= 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
