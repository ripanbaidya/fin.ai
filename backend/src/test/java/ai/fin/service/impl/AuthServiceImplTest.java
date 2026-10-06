package ai.fin.service.impl;

import ai.fin.config.properties.JwtProperties;
import ai.fin.dto.auth.LoginRequest;
import ai.fin.dto.auth.UserRegisterRequest;
import ai.fin.entities.RefreshToken;
import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.repository.RefreshTokenRepository;
import ai.fin.repository.UserRepository;
import ai.fin.security.jwt.JwtService;
import ai.fin.shared.exception.types.AuthException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private TokenBlackListServiceImpl tokenBlackListServiceImpl;

    @Mock
    private Claims accessClaims;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private JwtProperties.AccessToken accessConfig;
    private JwtProperties.RefreshToken refreshConfig;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("usr-12345");
        user.setEmail("john@example.com");
        user.setPasswordHash("hashed-password");
        user.setRole(Role.USER);
        user.setAccountStatus(AccountStatus.ACTIVE);

        accessConfig = new JwtProperties.AccessToken(900000L);
        refreshConfig = new JwtProperties.RefreshToken(604800000L);
    }

    @Test
    @DisplayName("Should successfully register a new user and issue tokens with userId")
    void shouldRegisterNewUser() {
        UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "Password123!");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtProperties.accessToken()).thenReturn(accessConfig);
        when(jwtProperties.refreshToken()).thenReturn(refreshConfig);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token-1");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token-1");

        var response = authService.registerUser(request);

        assertThat(response).isNotNull();
        assertThat(response.user().id()).isEqualTo("usr-12345");
        assertThat(response.token().accessToken()).isEqualTo("access-token-1");
        assertThat(response.token().refreshToken()).isEqualTo("refresh-token-1");
    }

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    void shouldLoginUserSuccessfully() {
        LoginRequest request = new LoginRequest("john@example.com", "Password123!");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "hashed-password")).thenReturn(true);
        when(jwtProperties.accessToken()).thenReturn(accessConfig);
        when(jwtProperties.refreshToken()).thenReturn(refreshConfig);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token-1");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token-1");

        var response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.user().id()).isEqualTo("usr-12345");
        assertThat(response.token().accessToken()).isEqualTo("access-token-1");
    }

    @Test
    @DisplayName("Should logout and blacklist access token using userId validation")
    void shouldLogoutSuccessfully() {
        String accessToken = "access-token-1";
        String refreshTokenStr = "refresh-token-1";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);

        when(jwtService.validateAccessToken(accessToken)).thenReturn(accessClaims);
        when(accessClaims.getSubject()).thenReturn("usr-12345");
        when(accessClaims.getId()).thenReturn("jti-access-1");
        when(refreshTokenRepository.findByToken(refreshTokenStr)).thenReturn(Optional.of(refreshToken));
        when(jwtService.getRemainingValidity(accessClaims)).thenReturn(Duration.ofMinutes(10));

        authService.logout(accessToken, refreshTokenStr);

        assertThat(refreshToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(refreshToken);
        verify(tokenBlackListServiceImpl).blacklist(eq("jti-access-1"), eq(Duration.ofMinutes(10).toMillis()));
    }

    @Test
    @DisplayName("Should reject logout when access token subject does not match refresh token user")
    void shouldRejectLogoutWhenUserMismatch() {
        String accessToken = "access-token-1";
        String refreshTokenStr = "refresh-token-1";

        User otherUser = new User();
        otherUser.setId("other-user-id");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(otherUser);

        when(jwtService.validateAccessToken(accessToken)).thenReturn(accessClaims);
        when(accessClaims.getSubject()).thenReturn("usr-12345");
        when(refreshTokenRepository.findByToken(refreshTokenStr)).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> authService.logout(accessToken, refreshTokenStr))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("Should delete account when password matches and token subject matches userId")
    void shouldDeleteAccountSuccessfully() {
        String accessToken = "access-token-1";

        when(jwtService.validateAccessToken(accessToken)).thenReturn(accessClaims);
        when(accessClaims.getSubject()).thenReturn("usr-12345");
        when(accessClaims.getId()).thenReturn("jti-access-1");
        when(userRepository.findById("usr-12345")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "hashed-password")).thenReturn(true);
        when(jwtService.getRemainingValidity(accessClaims)).thenReturn(Duration.ofMinutes(5));

        authService.deleteAccount("usr-12345", "Password123!", accessToken);

        assertThat(user.getAccountStatus()).isEqualTo(AccountStatus.DELETED);
        verify(userRepository).save(user);
        verify(tokenBlackListServiceImpl).blacklist(eq("jti-access-1"), eq(Duration.ofMinutes(5).toMillis()));
    }
}
