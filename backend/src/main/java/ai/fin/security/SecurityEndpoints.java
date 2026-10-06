package ai.fin.security;

import java.util.List;
import java.util.stream.Stream;

/**
 * Defines several categorized groups of endpoint URIs that are used
 * for security configuration within the application.
 */
public final class SecurityEndpoints {

    private SecurityEndpoints() {
    }

    public static final List<String> PUBLIC = List.of(
            "/error", "/auth/**"
    );

    public static final List<String> ACTUATOR = List.of(
            "/actuator/info",
            "/actuator/health/**",
            "/actuator/metrics"
    );

    public static final List<String> SWAGGER = List.of(
            "/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    public static List<String> ALL_PUBLIC = Stream.of(PUBLIC, ACTUATOR, SWAGGER)
            .flatMap(List::stream)
            .toList();
}