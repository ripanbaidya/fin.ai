package ai.fin.security.jwt;

import ai.fin.config.properties.JwtProperties;
import ai.fin.config.properties.RSAProperties;
import ai.fin.entities.User;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.JwtAuthenticationException;
import ai.fin.util.KeyUtils;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final int CLOCK_SKEW_SECONDS = 60;

    private final JwtProperties jwtProperties;
    private final RSAProperties rsaProperties;
    private final ResourceLoader resourceLoader;

    private PrivateKey privateKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void initialize() {
        log.info("Initializing JWT authentication with RSA keys.");

        log.info("RSA properties: {}", rsaProperties);
        log.info("Private key path: [{}]", rsaProperties.privateKeyPath());
        log.info("Public key path: [{}]", rsaProperties.publicKeyPath());

        String privateKeyPath = rsaProperties.privateKeyPath();
        String publicKeyPath = rsaProperties.publicKeyPath();

        if (!StringUtils.hasText(privateKeyPath) || !StringUtils.hasText(publicKeyPath)) {
            throw new IllegalStateException(
                    "Both private and public RSA key paths must be configured for JWT authentication."
            );
        }

        this.privateKey = KeyUtils.loadPrivateKey(privateKeyPath, resourceLoader);
        this.jwtParser = Jwts.parser()
                .verifyWith(KeyUtils.loadPublicKey(publicKeyPath, resourceLoader))
                .requireIssuer(jwtProperties.issuer())
                .clockSkewSeconds(CLOCK_SKEW_SECONDS)
                .build();

        log.info("JWT authentication initialized successfully.");
    }

    // Token issuing

    /**
     * Access token carries the authorization data (role, status, etc.) which are needed per request
     */
    public String generateAccessToken(User user) {
        JwtBuilder builder = baseToken(user, TokenType.ACCESS, jwtProperties.accessToken().expiry())
                .claim(JwtClaimNames.ROLE, user.getRole().name())
                .claim(JwtClaimNames.STATUS, user.getAccountStatus().name());
        return sign(builder);
    }

    /**
     * Refresh tokens are deliberately minimal, role and status are re-read from the
     * database when the token is exchanged, so a long-lived token never carries stale
     * authorization data.
     */
    public String generateRefreshToken(User user) {
        return sign(baseToken(user, TokenType.REFRESH, jwtProperties.refreshToken().expiry()));
    }

    private JwtBuilder baseToken(User user, TokenType type, long expiryMillis) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId())
                .claim(JwtClaimNames.EMAIL, user.getEmail())
                .issuer(jwtProperties.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiryMillis)))
                .claim(JwtClaimNames.TOKEN_TYPE, type.getValue());
    }

    private String sign(JwtBuilder builder) {
        return builder.signWith(privateKey, Jwts.SIG.RS512).compact();
    }

    // Token validation

    /**
     * Verifies signature, issuer, and expiry, and requires an access token with
     * a subject, jti, role, and status.
     */
    public Claims validateAccessToken(String token) {
        Claims claims = validate(token, TokenType.ACCESS);
        requireStringClaim(claims, JwtClaimNames.EMAIL);
        requireStringClaim(claims, JwtClaimNames.ROLE);
        requireStringClaim(claims, JwtClaimNames.STATUS);
        return claims;
    }

    /**
     * Verifies signature, issuer, and expiry, and requires a refresh token with a subject and jti.
     */
    public Claims validateRefreshToken(String token) {
        return validate(token, TokenType.REFRESH);
    }

    /**
     * Time left before the token expires, never negative.
     * Useful as the TTL when blacklisting a token on logout.
     */
    public Duration getRemainingValidity(Claims claims) {
        Duration remaining = Duration.between(Instant.now(), claims.getExpiration().toInstant());
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    private Claims validate(String token, TokenType expectedType) {
        Claims claims = parse(token);

        requireText(claims.getSubject(), "sub");
        requireText(claims.getId(), "jti");

        String actualType = requireStringClaim(claims, JwtClaimNames.TOKEN_TYPE);
        if (!expectedType.getValue().equals(actualType)) {
            log.warn("Token type mismatch — expected '{}' but got '{}'.", expectedType.getValue(), actualType);
            throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
        }
        return claims;
    }

    private Claims parse(String token) {
        if (!StringUtils.hasText(token)) {
            log.debug("Token parsing failed — token is null or blank.");
            throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
        }
        try {
            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (JwtException ex) {
            throw mapToAuthException(ex);
        }
    }

    private String requireStringClaim(Claims claims, String name) {
        try {
            String value = claims.get(name, String.class);
            requireText(value, name);
            return value;
        } catch (RequiredTypeException ex) {
            log.warn("Claim '{}' has an unexpected type.", name);
            throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
        }
    }

    private void requireText(String value, String claimName) {
        if (!StringUtils.hasText(value)) {
            log.warn("Token is missing required claim '{}'.", claimName);
            throw new JwtAuthenticationException(ErrorCode.TOKEN_MISSING_CLAIM);
        }
    }

    // Error mapping

    /**
     * Single place where jjwt exceptions are translated into our domain exception.
     */
    private JwtAuthenticationException mapToAuthException(JwtException ex) {
        if (ex instanceof ExpiredJwtException) {
            log.debug("Token expired.");
            return new JwtAuthenticationException(ErrorCode.TOKEN_EXPIRED);
        }
        if (ex instanceof SignatureException) {
            log.warn("Token rejected — RSA signature mismatch.");
            return new JwtAuthenticationException(ErrorCode.TOKEN_SIGNATURE_INVALID);
        }
        if (ex instanceof MalformedJwtException || ex instanceof UnsupportedJwtException) {
            log.warn("Token rejected — malformed or unsupported.");
            return new JwtAuthenticationException(ErrorCode.TOKEN_UNSUPPORTED);
        }
        log.warn("Token rejected — {}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
    }
}