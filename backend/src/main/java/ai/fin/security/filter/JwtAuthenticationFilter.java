package ai.fin.security.filter;

import ai.fin.config.properties.JwtProperties;
import ai.fin.security.RestAuthenticationEntryPoint;
import ai.fin.security.SecurityEndpoints;
import ai.fin.security.UserPrincipal;
import ai.fin.security.jwt.JwtService;
import ai.fin.service.TokenBlackListService;
import ai.fin.shared.exception.types.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtProperties jwtProperties;
    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(jwtProperties.header()); // "Authorization"
        String prefix = jwtProperties.prefix(); // "Bearer "

        // No bearer token: continue unauthenticated
        // SecurityConfig returns 401 for protected endpoints.
        if (header == null || !header.startsWith(prefix)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(prefix.length()).trim();
        if (!authenticate(token, request)) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid bearer token"));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean authenticate(String token, HttpServletRequest request) {
        try {
            Claims claims = jwtService.validateAccessToken(token);
            if (tokenBlackListService.isBlacklisted(claims.getId())) {
                log.debug("Bearer token rejected — token is blacklisted.");
                return false;
            }

            UserPrincipal principal = UserPrincipal.fromClaims(claims);
            if (!principal.isEnabled()) {
                log.debug("Bearer token rejected — user account is not active.");
                return false;
            }

            var authentication = UsernamePasswordAuthenticationToken.authenticated(
                    principal, null, principal.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            return true;
        } catch (JwtAuthenticationException ex) {
            log.debug("Bearer token rejected — {}", ex.getMessage());
            return false;
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /**
     * Public endpoints are not authenticated.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return Arrays.stream(SecurityEndpoints.ALL_PUBLIC.toArray(String[]::new))
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }
}
