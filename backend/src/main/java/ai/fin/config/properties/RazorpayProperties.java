package ai.fin.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.features.payment.razorpay")
public record RazorpayProperties(
        boolean enabled,
        String keyId,
        String keySecret,
        String currency,
        int subscriptionAmount,
        int subscriptionDays
) {
}