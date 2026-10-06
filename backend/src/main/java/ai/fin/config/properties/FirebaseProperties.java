package ai.fin.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.firebase")
public record FirebaseProperties(

        boolean enabled,
        String credentialsPath
) {
}