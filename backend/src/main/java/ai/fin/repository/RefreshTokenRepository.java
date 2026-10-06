package ai.fin.repository;

import ai.fin.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    /**
     * Find a refresh token by token.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find an active refresh token by user id.
     */
    @Query("select rt from RefreshToken rt where rt.user.id = :userId and rt.revoked = false and rt.expiresAt > :now")
    Optional<RefreshToken> findActiveByUserId(@Param("userId") String userId, @Param("now") Instant now);

    /**
     * Delete all refresh tokens that are either expired or revoked.
     * Schedular will use this to clean up expired tokens to prevent unbounded growth of the table.
     */
    @Modifying
    @Query("delete from RefreshToken rt where rt.expiresAt < :now or rt.revoked = true")
    int deleteAllInactiveTokens(@Param("now") Instant now);

    /**
     * Revoke every currently active refresh token for a user.
     * This is a defensive safeguard alongside the single-active-session model: if a race condition
     * creates multiple valid tokens temporarily, all of them are invalidated together.
     * Used during account deletion to immediately end all user sessions.
     */
    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true, rt.revokedAt = :now where rt.user.id = :userId and rt.revoked = false")
    int revokeAllActiveTokensByUserId(@Param("userId") String userId, @Param("now") Instant now);

}
