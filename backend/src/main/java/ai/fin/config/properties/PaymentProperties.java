package ai.fin.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "app.features.payment")
public record PaymentProperties(

        @NotNull @Valid RazorpayConfig razorpay,
        @NotNull @Valid Map<String, PlanConfig> plans
) {

    @Validated
    public record RazorpayConfig(

            boolean enabled,
            @NotBlank String keyId,
            @NotBlank String keySecret,
            @NotBlank String currency,
            String webhookSecret
    ) {
    }

    @Validated
    public record PlanConfig(

            @NotNull @Min(1) Long priceInPaise,
            Integer durationDays
    ) {
    }
}
