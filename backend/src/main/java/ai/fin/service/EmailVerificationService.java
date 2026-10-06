package ai.fin.service;

public interface EmailVerificationService {

    /**
     * Send OTP to the user's email.
     */
    void sendOtp(String email);

    /**
     * Verify OTP against the user's email.
     */
    void verifyOtp(String email, String otp);

    /**
     * Resend OTP to the user's email.
     */
    void resendOtp(String email);
}