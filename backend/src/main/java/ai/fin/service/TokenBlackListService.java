package ai.fin.service;

public interface TokenBlackListService {

    /**
     * Adds an access token's JTI to the blacklist.
     * TTL is set to the token's remaining validity, so the entry is auto-evicted
     * once the token would be expired anyway.
     *
     * @param jti                       the {@code jti} claim from the JWT (must not be blank)
     * @param remainingValidityInMillis milliseconds until the token naturally expires
     */
    void blacklist(String jti, long remainingValidityInMillis);

    /**
     * Checks whether a token's JTI is present in the blacklist.
     *
     * @param jti the {@code jti} claim extracted from the incoming JWT
     * @return {@code true} if the token has been explicitly revoked, {@code false} otherwise
     */
    boolean isBlacklisted(String jti);
}