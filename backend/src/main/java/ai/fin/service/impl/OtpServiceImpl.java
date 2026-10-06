package ai.fin.service.impl;

import ai.fin.config.properties.OtpProperties;
import ai.fin.service.OtpService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.OtpException;
import ai.fin.util.SecurityValueGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final String CODE_KEY_PREFIX = "otp:code:";
    private static final String ATTEMPTS_KEY_PREFIX = "otp:attempts:";
    private static final String COOLDOWN_KEY_PREFIX = "otp:cooldown:";

    private final OtpProperties otpProperties;
    private final StringRedisTemplate redisTemplate;

    @Override
    public String generate(String identifier) {
        enforceCooldown(identifier);

        String otp = SecurityValueGenerator.generateOtp(otpProperties.length());
        Duration ttl = Duration.ofMillis(otpProperties.ttl());

        redisTemplate.opsForValue().set(key(CODE_KEY_PREFIX, identifier), otp, ttl);
        // The counter shares the same TTL as the OTP, so INCR while verifying keeps
        // that expiry
        redisTemplate.opsForValue().set(key(ATTEMPTS_KEY_PREFIX, identifier), "0", ttl);

        log.debug("OTP generated for identifier='{}'", identifier);
        return otp;
    }

    @Override
    public void verify(String identifier, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(key(CODE_KEY_PREFIX, identifier));
        if (storedOtp == null) {
            throw new OtpException(ErrorCode.OTP_EXPIRED);
        }
        Long attempts = redisTemplate.opsForValue().increment(key(ATTEMPTS_KEY_PREFIX, identifier));

        if (!matches(storedOtp, otp)) {
            if (attempts != null && attempts >= otpProperties.maxAttempts()) {
                throw new OtpException(ErrorCode.OTP_MAX_ATTEMPTS_EXCEEDED);
            }
            throw new OtpException(ErrorCode.OTP_INVALID);
        }

        invalidate(identifier);
        log.debug("OTP verified for identifier='{}', attempts={}", identifier, attempts);
    }

    @Override
    public void invalidate(String identifier) {
        redisTemplate.delete(List.of(
                key(CODE_KEY_PREFIX, identifier), // otp
                key(ATTEMPTS_KEY_PREFIX, identifier))); // attempt
    }

    @Override
    public long getCooldownRemainingSeconds(String identifier) {
        Long second = redisTemplate.getExpire(key(COOLDOWN_KEY_PREFIX, identifier));
        return second == null || second < 0 ? 0 : second;
    }

    // Helpers

    /**
     * Atomically starts the cooldown.
     * If the key already exists, a recent OTP was issued and the request is rejected.
     */
    private void enforceCooldown(String identifier) {
        Duration ttl = Duration.ofSeconds(otpProperties.cooldownSeconds());
        Boolean started = redisTemplate.opsForValue().setIfAbsent(key(COOLDOWN_KEY_PREFIX, identifier), "1", ttl);

        if (!Boolean.TRUE.equals(started)) {
            throw new OtpException(
                    ErrorCode.OTP_COOLDOWN_ACTIVE, "Please wait %d seconds before requesting a new OTP"
                    .formatted(getCooldownRemainingSeconds(identifier))
            );
        }
    }

    /**
     * Constant-time comparison to avoid leaking information through timing.
     *
     * @param expected the expected otp (e.g., the one that was sent to the user's)
     * @param actual   the actual otp that the user submitted
     * @return true if the two otp match, false otherwise
     */
    private boolean matches(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate a key for the given prefix and identifier.
     */
    private String key(String prefix, String identifier) {
        return prefix + identifier;
    }

}
