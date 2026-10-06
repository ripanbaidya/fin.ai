package ai.fin.service.impl;

import ai.fin.config.properties.OtpProperties;
import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.repository.UserRepository;
import ai.fin.service.EmailService;
import ai.fin.service.EmailVerificationService;
import ai.fin.service.OtpService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final OtpProperties otpProperties;

    private final OtpService otpService;
    private final EmailService emailService;

    private final UserRepository userRepository;

    @Override
    public void sendOtp(String email) {
        User user = findUserByEmail(email);

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            log.debug("Send verification OTP skipped — already ACTIVE for email='{}'", email);
            throw new AuthException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        if (user.getAccountStatus() != AccountStatus.PENDING_VERIFICATION) {
            log.warn("Send verification OTP rejected — unexpected status={} for email='{}'", user.getAccountStatus(), email);
            throw new AuthException(ErrorCode.ACCOUNT_DELETED);
        }

        dispatchOtp(user);
    }

    @Override
    public void verifyOtp(String email, String otp) {
        User user = findUserByEmail(email);

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            log.debug("Email verification skipped — already ACTIVE for email='{}'", email);
            throw new AuthException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        // Any other terminal/blocked state (e.g., DELETED) should not be reactivated via this path.
        if (user.getAccountStatus() != AccountStatus.PENDING_VERIFICATION) {
            log.warn("Email verification rejected — unexpected status={} for email='{}'", user.getAccountStatus(), email);
            throw new AuthException(ErrorCode.ACCOUNT_DELETED);
        }

        otpService.verify(email, otp);

        // Change the account status
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        log.info("Email verified successfully — userId='{}'", user.getId());
    }

    @Override
    public void resendOtp(String email) {
        User user = findUserByEmail(email);

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            log.debug("Resend verification skipped — already ACTIVE for email='{}'", email);
            throw new AuthException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        if (user.getAccountStatus() != AccountStatus.PENDING_VERIFICATION) {
            log.warn("Resend verification rejected — unexpected status={} for email='{}'", user.getAccountStatus(), email);
            throw new AuthException(ErrorCode.ACCOUNT_DELETED);
        }

        dispatchOtp(user);
    }

    // Helpers

    private void dispatchOtp(User user) {
        String email = user.getEmail();
        String otp = otpService.generate(email);
        long validityMinutes = Duration.ofMillis(otpProperties.ttl()).toMinutes();
        Map<String, Object> model = Map.of(
                "name", user.getFullName(),
                "otp", otp,
                "validityMinutes", validityMinutes
        );

        emailService.sendHtmlEmail(email, "Email verification", "email/authentication/verify-email", model);
        log.info("Verification OTP dispatched to email='{}'", email);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Verification operation failed — no account found for email='{}'", email);
            return new AuthException(ErrorCode.USER_NOT_FOUND);
        });
    }
}