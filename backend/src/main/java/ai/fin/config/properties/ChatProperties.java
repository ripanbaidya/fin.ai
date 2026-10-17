package ai.fin.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.chat")
public record ChatProperties(

        @NotNull @Min(0) Integer freeChatLimit
) {
}
