package ai.fin.service.impl;

import ai.fin.config.properties.JwtProperties;
import ai.fin.dto.auth.AuthResponse;
import ai.fin.dto.auth.LoginRequest;
import ai.fin.dto.auth.TokenResponse;
import ai.fin.dto.auth.UserRegisterRequest;
import ai.fin.entities.RefreshToken;
import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.repository.RefreshTokenRepository;
import ai.fin.repository.UserRepository;
import ai.fin.security.jwt.JwtService;
import ai.fin.service.AuthService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.AuthException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProperties jwtProperties;

    private final JwtService jwtService;
    private final TokenBlackListServiceImpl tokenBlackListServiceImpl;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    @Transactional
    public AuthResponse registerUser(UserRegisterRequest request) {
        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            log.warn("Account creation failed. email already exists: {}", email);
            throw new AuthException(ErrorCode.EMAIL_ALREADY_IN_USE);
        }

        User user = new User();
        user.setEmail(email);
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
        user = userRepository.save(user);

        log.info("User identity persisted — userId='{}'.", user.getId());

        TokenResponse tokens = issueTokenPair(user);
        log.info("User registered and tokens issued — userId='{}'.", user.getId());
        return AuthResponse.of(user, tokens);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Login failed. no account found for email={}", email);
            return new AuthException(ErrorCode.USER_NOT_FOUND);
        });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Login failed. password does not match for email={}", email);
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
        }

        ensureAccountNotDeleted(user);

        TokenResponse tokens = issueTokenPair(user);
        log.info("Login successful — userId='{}', role='{}'.", user.getId(), user.getRole());
        return AuthResponse.of(user, tokens);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        // Signature, issuer, expiry and token type (must be REFRESH) are all verified here.
        jwtService.validateRefreshToken(refreshToken);

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> {
            log.warn("Refresh token exchange rejected — token not found in database.");
            return new AuthException(ErrorCode.TOKEN_NOT_FOUND,
                    "Refresh token not recognised. Please log in again.");
        });
        User user = stored.getUser();

        if (stored.isRevoked()) {
            log.warn("Refresh token exchange rejected — token already revoked. " +
                    "Possible replay attack for userId='{}'.", user.getId());
            throw new AuthException(ErrorCode.TOKEN_REVOKED,
                    "This refresh token has already been used or revoked. Please log in again.");
        }
        if (stored.isExpired()) {
            log.warn("Refresh token exchange rejected — token expired in DB for userId='{}'.", user.getId());
            throw new AuthException(ErrorCode.TOKEN_EXPIRED,
                    "Your session has expired. Please log in again.");
        }

        // Refresh tokens carry no role/status, so the user's current state is checked here.
        ensureAccountNotDeleted(user);

        // Rotate: revoke the old token and issue a completely fresh pair
        stored.revoke(Instant.now());
        refreshTokenRepository.save(stored);
        log.debug("Old refresh token rotated (revoked) for userId='{}'.", user.getId());

        TokenResponse newTokens = issueTokenPair(user);

        log.info("Token pair refreshed successfully — userId='{}'.", user.getId());
        return newTokens;
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        Claims accessClaims = jwtService.validateAccessToken(accessToken);

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> {
            log.warn("Logout failed — refresh token not found.");
            return new AuthException(ErrorCode.TOKEN_NOT_FOUND, "Refresh token not found!");
        });
        User user = stored.getUser();

        // The refresh token must belong to the same user as the access token, otherwise
        // any logged-in user could revoke someone else's session.
        if (!user.getId().equals(accessClaims.getSubject())) {
            log.warn("Logout rejected — refresh token does not belong to the authenticated user. userId='{}'.",
                    user.getId());
            throw new AuthException(ErrorCode.TOKEN_INVALID,
                    "The refresh token does not belong to the authenticated user.");
        }

        if (stored.isRevoked()) {
            log.warn("Logout called with an already-revoked refresh token for userId='{}'."
                    + " Proceeding to blacklist access token anyway.", user.getId());
        } else {
            stored.revoke(Instant.now());
            refreshTokenRepository.save(stored);
            log.debug("Refresh token revoked for userId='{}'.", user.getId());
        }

        blacklistAccessToken(accessClaims, user.getId());

        log.info("Logout successful — userId='{}'.", user.getId());
    }

    @Override
    @Transactional
    public void deleteAccount(String userId, String password, String accessToken) {
        Claims accessClaims = jwtService.validateAccessToken(accessToken);

        // Security check: ensure the accessToken belongs to the same user
        if (!userId.equals(accessClaims.getSubject())) {
            log.warn("Account deletion rejected — token subject mismatch. providedUserId='{}', tokenSub='{}'",
                    userId, accessClaims.getSubject());
            throw new AuthException(ErrorCode.TOKEN_INVALID, "Access token does not belong to the authenticated user.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Account deletion failed — no user found for userId={}", userId);
            return new AuthException(ErrorCode.USER_NOT_FOUND);
        });

        if (user.getAccountStatus() == AccountStatus.DELETED) {
            log.warn("Account deletion rejected — already pending deletion, userId={}", user.getId());
            throw new AuthException(ErrorCode.ACCOUNT_DELETED);
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Account deletion rejected — password mismatch, userId={}", user.getId());
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS, "Password does not match the current account password.");
        }

        Instant now = Instant.now();
        user.setAccountStatus(AccountStatus.DELETED);
        user.setDeletionRequestedAt(now);
        userRepository.save(user);

        int revoked = refreshTokenRepository.revokeAllActiveTokensByUserId(user.getId(), now);
        log.debug("Revoked {} active refresh token(s) for userId={} during account deletion.", revoked, user.getId());

        // Blacklist the access token used for this very request so it can't be reused for its remaining lifetime.
        blacklistAccessToken(accessClaims, user.getId());

        log.info("Account deletion requested — userId='{}'. Account will be permanently deleted after the grace period.",
                user.getId());
    }

    // Helpers

    private TokenResponse issueTokenPair(User user) {
        // Revoke any existing active refresh token — enforce single active session
        refreshTokenRepository.findActiveByUserId(user.getId(), Instant.now()).ifPresent(existing -> {
            existing.revoke(Instant.now());
            refreshTokenRepository.save(existing);
            log.debug("Revoked previous active refresh token for userId='{}'.", user.getId());
        });

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Instant now = Instant.now();
        long refreshExpiryMillis = jwtProperties.refreshToken().expiry();
        long accessExpiryMillis = jwtProperties.accessToken().expiry();

        RefreshToken entity = new RefreshToken();
        entity.setToken(refreshToken);
        entity.setUser(user);
        entity.setCreatedAt(now);
        entity.setExpiresAt(now.plusMillis(refreshExpiryMillis));
        entity.setRevoked(false);
        refreshTokenRepository.save(entity);

        log.debug("Token pair issued — userId='{}', refreshTokenExpiresAt='{}'.", user.getId(), entity.getExpiresAt());
        return TokenResponse.of(accessToken, refreshToken, accessExpiryMillis);
    }

    private void ensureAccountNotDeleted(User user) {
        if (user.getAccountStatus() == AccountStatus.DELETED) {
            log.warn("Request rejected — account is deleted, userId='{}'.", user.getId());
            throw new AuthException(ErrorCode.ACCOUNT_DELETED);
        }
    }

    /**
     * Best-effort: if the blacklist store is unavailable, the token simply lives out its
     * remaining lifetime, and the logout / deletion itself must not fail because of it.
     */
    private void blacklistAccessToken(Claims accessClaims, String userId) {
        try {
            long remainingMillis = jwtService.getRemainingValidity(accessClaims).toMillis();
            tokenBlackListServiceImpl.blacklist(accessClaims.getId(), remainingMillis);
        } catch (RuntimeException e) {
            log.warn("Could not blacklist access token — it will expire on its own. userId='{}', reason: {}.",
                    userId, e.getMessage());
        }
    }
}