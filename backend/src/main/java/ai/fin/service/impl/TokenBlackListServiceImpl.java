package ai.fin.service.impl;

import ai.fin.service.TokenBlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlackListServiceImpl implements TokenBlackListService {

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String REVOKED_MARKER = "revoked";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String jti, long remainingValidityInMillis) {
        if (!StringUtils.hasText(jti)) {
            log.warn("Refusing to blacklist token with null/blank jti: {}", jti);
            return;
        }

        if (remainingValidityInMillis <= 0) {
            log.debug("Skipping blacklist for jti='{}' — token is already expired.", jti);
            return;
        }

        try {
            redisTemplate.opsForValue().set(key(jti), REVOKED_MARKER, Duration.ofMillis(remainingValidityInMillis));
            log.info("Access token blacklisted, jti='{}', expires in {}ms.", jti, remainingValidityInMillis);
        } catch (Exception e) {
            log.error("Failed to blacklist jti='{}' — Redis unavailable?", jti, e);
            throw e;
        }
    }

    @Override
    public boolean isBlacklisted(String jti) {
        if (!StringUtils.hasText(jti)) {
            return false;
        }

        boolean blacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(key(jti)));
        if (blacklisted) {
            log.warn("Blacklisted token detected: {}", jti);
        }
        return blacklisted;
    }

    private String key(String jti) {
        return BLACKLIST_PREFIX + jti;
    }
}
