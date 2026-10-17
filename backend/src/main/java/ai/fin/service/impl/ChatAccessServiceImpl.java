package ai.fin.service.impl;

import ai.fin.config.properties.ChatProperties;
import ai.fin.entities.User;
import ai.fin.enums.SubscriptionStatus;
import ai.fin.repository.SubscriptionRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.ChatAccessService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatAccessServiceImpl implements ChatAccessService {

    private final ChatProperties chatProperties;

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public void checkAccess(String userId) {
        if (hasActiveSubscription(userId)) {
            return;
        }

        User user = getUser(userId);
        if (user.getFreeChatCount() >= chatProperties.freeChatLimit()) {
            throw new BusinessException(ErrorCode.SUBSCRIPTION_REQUIRED,
                    "Free chat limit reached. Please subscribe to continue using AI chat.");
        }
    }

    @Override
    @Transactional
    public void incrementFreeUsage(String userId) {
        if (hasActiveSubscription(userId)) {
            return;
        }

        User user = getUser(userId);
        user.setFreeChatCount(user.getFreeChatCount() + 1);
        userRepository.save(user);
    }

    private boolean hasActiveSubscription(String userId) {
        return subscriptionRepository.findByUser_IdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .map(sub -> sub.getEndDate() == null || sub.getEndDate().isAfter(Instant.now()))
                .orElse(false);
    }

    private User getUser(String userId) {
        return userRepository.getReferenceById(userId);
    }
}
