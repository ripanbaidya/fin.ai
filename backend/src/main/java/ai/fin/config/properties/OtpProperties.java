package ai.fin.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.otp")
public record OtpProperties(
        int length,
        int maxAttempts,
        long ttl,
        long cooldownSeconds
) {
}
