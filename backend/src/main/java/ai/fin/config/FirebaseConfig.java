package ai.fin.config;

import ai.fin.config.properties.FirebaseProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.firebase", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    private final FirebaseProperties properties;
    private final ResourceLoader resourceLoader;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        String path = properties.credentialsPath();
        if (path == null || path.isBlank()) {
            path = "classpath:credentials.json";
        }

        String location = path.startsWith("classpath:") || path.startsWith("file:")
                ? path
                : "classpath:" + path;

        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            resource = resourceLoader.getResource("file:" + path);
        }

        if (!resource.exists()) {
            log.warn("Firebase credentials file not found at: {}. FCM push notifications will be disabled until valid credentials are provided.", path);
            return null;
        }

        try (InputStream in = resource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(in))
                    .build();
            FirebaseApp app = FirebaseApp.getApps().isEmpty()
                    ? FirebaseApp.initializeApp(options)
                    : FirebaseApp.getInstance();

            log.info("FirebaseMessaging successfully initialized using credentials at {}", location);
            return FirebaseMessaging.getInstance(app);
        } catch (IOException e) {
            log.error("Failed to initialize FirebaseMessaging from {}: {}", location, e.getMessage());
            return null;
        }
    }
}
