package ai.fin.service.impl;

import ai.fin.dto.devices.RegisterDeviceTokenRequest;
import ai.fin.dto.devices.UnregisterDeviceTokenRequest;
import ai.fin.entities.DeviceToken;
import ai.fin.entities.User;
import ai.fin.repository.DeviceTokenRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.DeviceTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final UserRepository userRepository;
    private final DeviceTokenRepository deviceTokenRepository;

    @Override
    @Transactional
    public void register(String userId, RegisterDeviceTokenRequest request) {
        User user = userRepository.getReferenceById(userId);
        Optional<DeviceToken> existingOpt = deviceTokenRepository.findByToken(request.token());

        DeviceToken deviceToken;
        if (existingOpt.isPresent()) {
            deviceToken = existingOpt.get();
            deviceToken.setUser(user);
            deviceToken.setPlatform(request.platform());
            deviceToken.setLastSeenAt(Instant.now());
            log.debug("Updated existing device token for user: {}", userId);
        } else {
            deviceToken = new DeviceToken();
            deviceToken.setUser(user);
            deviceToken.setToken(request.token());
            deviceToken.setPlatform(request.platform());
            deviceToken.setLastSeenAt(Instant.now());
            log.debug("Registered new device token for user: {}", userId);
        }

        deviceTokenRepository.saveAndFlush(deviceToken);
    }

    @Override
    @Transactional
    public void unregister(String userId, UnregisterDeviceTokenRequest request) {
        int deletedCount = deviceTokenRepository.deleteByTokenAndUser_Id(request.token(), userId);
        log.debug("Unregistered device token for user: {}, count: {}", userId, deletedCount);
    }

    @Override
    @Transactional
    public void unregisterAll(String userId) {
        deviceTokenRepository.deleteAllByUser_Id(userId);
        log.debug("Unregistered all device tokens for user: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getActiveTokens(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return deviceTokenRepository.findByUser_Id(userId).stream()
                .map(DeviceToken::getToken)
                .toList();
    }

    @Override
    @Transactional
    public void removeInvalidTokens(Collection<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }
        int deleted = deviceTokenRepository.deleteAllByTokenIn(tokens);
        log.info("Removed {} invalid/unregistered FCM device tokens from database", deleted);
    }

    @Override
    @Transactional
    public int purgeStaleTokens(int inactiveDays) {
        Instant cutoff = Instant.now().minus(Duration.ofDays(inactiveDays));
        int purged = deviceTokenRepository.deleteStale(cutoff);
        log.info("Purged {} stale device tokens inactive for more than {} days", purged, inactiveDays);
        return purged;
    }
}
