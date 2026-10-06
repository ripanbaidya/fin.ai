package ai.fin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String DESCRIPTION = """
            This is the API documentation for the Fin.ai application.
            It provides information about the available endpoints, request/response formats, and authentication requirements.
            """;

    @Value("${info.app.name}")
    private String name;

    @Value("${info.app.version}")
    private String version;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(name)
                        .version(version)
                        .description(DESCRIPTION)
                        .contact(new Contact()
                                .email("connect.ripanbaidya@gmail.com")
                        )
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        )
                ).components(new Components()
                        .addSecuritySchemes(
                                BEARER_PREFIX,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"")
                        ));
    }
}
