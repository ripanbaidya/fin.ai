package ai.fin.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        String issuer,
        String header,
        String prefix,
        AccessToken accessToken,
        RefreshToken refreshToken
) {
    public record AccessToken(
            long expiry
    ) {
    }

    public record RefreshToken(
            long expiry
    ) {
    }
}