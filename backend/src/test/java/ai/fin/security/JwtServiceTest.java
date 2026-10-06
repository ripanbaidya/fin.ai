package ai.fin.security;

import ai.fin.config.properties.JwtProperties;
import ai.fin.config.properties.RSAProperties;
import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.security.jwt.JwtClaimNames;
import ai.fin.security.jwt.JwtService;
import ai.fin.security.jwt.TokenType;
import ai.fin.shared.exception.types.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private ResourceLoader resourceLoader;

    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        String privatePem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(keyPair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";

        String publicPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(keyPair.getPublic().getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        when(resourceLoader.getResource("classpath:certs/private.pem"))
                .thenReturn(new ByteArrayResource(privatePem.getBytes(StandardCharsets.UTF_8)));
        when(resourceLoader.getResource("classpath:certs/public.pem"))
                .thenReturn(new ByteArrayResource(publicPem.getBytes(StandardCharsets.UTF_8)));

        JwtProperties jwtProperties = new JwtProperties(
                "walletiq-auth-service",
                "Authorization",
                "Bearer ",
                new JwtProperties.AccessToken(900000L),
                new JwtProperties.RefreshToken(604800000L)
        );

        RSAProperties rsaProperties = new RSAProperties(
                "classpath:certs/private.pem",
                "classpath:certs/public.pem"
        );

        jwtService = new JwtService(jwtProperties, rsaProperties, resourceLoader);
        jwtService.initialize();

        testUser = new User();
        testUser.setId("user-uuid-1234");
        testUser.setEmail("test@walletiq.ai");
        testUser.setPasswordHash("hashed_password");
        testUser.setRole(Role.USER);
        testUser.setAccountStatus(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should generate access token with userId as sub and email as claim")
    void shouldGenerateAccessTokenWithUserIdAsSubject() {
        String token = jwtService.generateAccessToken(testUser);
        assertThat(token).isNotBlank();

        Claims claims = jwtService.validateAccessToken(token);

        assertThat(claims.getSubject()).isEqualTo("user-uuid-1234");
        assertThat(claims.get(JwtClaimNames.EMAIL, String.class)).isEqualTo("test@walletiq.ai");
        assertThat(claims.get(JwtClaimNames.ROLE, String.class)).isEqualTo("USER");
        assertThat(claims.get(JwtClaimNames.STATUS, String.class)).isEqualTo("ACTIVE");
        assertThat(claims.get(JwtClaimNames.TOKEN_TYPE, String.class)).isEqualTo(TokenType.ACCESS.getValue());
        assertThat(claims.getId()).isNotBlank();
        assertThat(claims.getIssuer()).isEqualTo("walletiq-auth-service");
    }

    @Test
    @DisplayName("Should generate refresh token with userId as sub and email as claim")
    void shouldGenerateRefreshTokenWithUserIdAsSubject() {
        String token = jwtService.generateRefreshToken(testUser);
        assertThat(token).isNotBlank();

        Claims claims = jwtService.validateRefreshToken(token);

        assertThat(claims.getSubject()).isEqualTo("user-uuid-1234");
        assertThat(claims.get(JwtClaimNames.EMAIL, String.class)).isEqualTo("test@walletiq.ai");
        assertThat(claims.get(JwtClaimNames.TOKEN_TYPE, String.class)).isEqualTo(TokenType.REFRESH.getValue());
    }

    @Test
    @DisplayName("Should reject token type mismatch when validating access token with refresh token")
    void shouldRejectTokenTypeMismatch() {
        String refreshToken = jwtService.generateRefreshToken(testUser);

        assertThatThrownBy(() -> jwtService.validateAccessToken(refreshToken))
                .isInstanceOf(JwtAuthenticationException.class);
    }

    @Test
    @DisplayName("Should calculate remaining validity correctly")
    void shouldCalculateRemainingValidity() {
        String token = jwtService.generateAccessToken(testUser);
        Claims claims = jwtService.validateAccessToken(token);

        Duration remaining = jwtService.getRemainingValidity(claims);
        assertThat(remaining.toSeconds()).isGreaterThan(0);
    }
}
