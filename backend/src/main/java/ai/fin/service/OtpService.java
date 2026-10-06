package ai.fin.service;

import ai.fin.shared.exception.types.OtpException;

/**
 * Manages the OTP lifecycle (generation, verification, invalidation) using Redis.
 *
 */
public interface OtpService {

    /**
     * Generates and stores a new OTP, replacing any existing one for the identifier.
     * Resets the failed-attempt counter and starts the resend cooldown.
     *
     * @param identifier the key the OTP is bound to (e.g., email)
     * @return the generated OTP, to be delivered to the user
     * @throws OtpException if the reset cooldown has not elapsed
     */
    String generate(String identifier);

    /**
     * Verifies the OTP for the identifier. successful verification consumes the OTP,
     * so it cannot be reused.
     *
     * @param identifier the key the OTP is bound to
     * @param otp        the OTP submitted by the user
     * @throws OtpException if the OTP is expired, invalid, or max attempts are exceeded
     */
    void verify(String identifier, String otp);

    /**
     * Invalidates the active OTP and its attempt counter for the identifier.
     * The resend cooldown is left untouched.
     */
    void invalidate(String identifier);

    /**
     * Returns the remaining resend cooldown in seconds, or 0 if a new OTP can be requested.
     *
     * @param identifier the key the OTP is bound to
     * @return the remaining cooldown in seconds, or 0 if a new OTP can be requested
     */
    long getCooldownRemainingSeconds(String identifier);
}
