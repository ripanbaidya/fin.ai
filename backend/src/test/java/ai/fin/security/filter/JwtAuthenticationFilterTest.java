package ai.fin.security.filter;

import ai.fin.config.properties.JwtProperties;
import ai.fin.security.RestAuthenticationEntryPoint;
import ai.fin.security.UserPrincipal;
import ai.fin.security.jwt.JwtClaimNames;
import ai.fin.security.jwt.JwtService;
import ai.fin.service.TokenBlackListService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlackListService tokenBlackListService;

    @Mock
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtProperties = new JwtProperties(
                "walletiq-auth-service",
                "Authorization",
                "Bearer ",
                new JwtProperties.AccessToken(900000L),
                new JwtProperties.RefreshToken(604800000L)
        );
        filter = new JwtAuthenticationFilter(jwtProperties, jwtService, tokenBlackListService, authenticationEntryPoint);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate valid token and populate SecurityContext without database queries")
    void shouldAuthenticateValidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.validateAccessToken("valid-token")).thenReturn(claims);
        when(claims.getId()).thenReturn("jti-123");
        when(claims.getSubject()).thenReturn("usr-uuid-1");
        when(claims.get(JwtClaimNames.EMAIL, String.class)).thenReturn("test@walletiq.ai");
        when(claims.get(JwtClaimNames.ROLE, String.class)).thenReturn("USER");
        when(claims.get(JwtClaimNames.STATUS, String.class)).thenReturn("ACTIVE");
        when(tokenBlackListService.isBlacklisted("jti-123")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isInstanceOf(UserPrincipal.class);

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        assertThat(principal.getId()).isEqualTo("usr-uuid-1");
        assertThat(principal.getEmail()).isEqualTo("test@walletiq.ai");
        assertThat(principal.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");

        verify(filterChain).doFilter(request, response);
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("Should pass request without auth header unauthenticated to allow SecurityConfig to handle it")
    void shouldPassWithoutAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("Should reject blacklisted token via entry point")
    void shouldRejectBlacklistedToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer blacklisted-token");
        when(jwtService.validateAccessToken("blacklisted-token")).thenReturn(claims);
        when(claims.getId()).thenReturn("jti-blacklisted");
        when(tokenBlackListService.isBlacklisted("jti-blacklisted")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(authenticationEntryPoint).commence(any(), any(), any());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should reject token when user status is DELETED")
    void shouldRejectTokenWhenUserIsDeleted() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer deleted-user-token");
        when(jwtService.validateAccessToken("deleted-user-token")).thenReturn(claims);
        when(claims.getId()).thenReturn("jti-deleted");
        when(claims.getSubject()).thenReturn("usr-deleted");
        when(claims.get(JwtClaimNames.EMAIL, String.class)).thenReturn("deleted@walletiq.ai");
        when(claims.get(JwtClaimNames.ROLE, String.class)).thenReturn("USER");
        when(claims.get(JwtClaimNames.STATUS, String.class)).thenReturn("DELETED");
        when(tokenBlackListService.isBlacklisted("jti-deleted")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(authenticationEntryPoint).commence(any(), any(), any());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should reject invalid or expired token throwing JwtAuthenticationException")
    void shouldRejectInvalidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
        when(jwtService.validateAccessToken("expired-token"))
                .thenThrow(new JwtAuthenticationException(ErrorCode.TOKEN_EXPIRED));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(authenticationEntryPoint).commence(any(), any(), any());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should skip filtering for public endpoints")
    void shouldSkipPublicEndpoints() {
        when(request.getRequestURI()).thenReturn("/auth/login");
        when(request.getContextPath()).thenReturn("");

        boolean shouldNotFilter = filter.shouldNotFilter(request);
        assertThat(shouldNotFilter).isTrue();
    }

    @Test
    @DisplayName("Should not skip async dispatch filtering")
    void shouldNotSkipAsyncDispatch() {
        assertThat(filter.shouldNotFilterAsyncDispatch()).isFalse();
    }
}
