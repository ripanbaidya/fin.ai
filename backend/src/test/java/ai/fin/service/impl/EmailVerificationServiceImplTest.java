package ai.fin.service.impl;

import ai.fin.config.properties.OtpProperties;
import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.repository.UserRepository;
import ai.fin.service.EmailService;
import ai.fin.service.OtpService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private OtpProperties otpProperties;

    @Mock
    private OtpService otpService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    private User pendingUser;
    private User activeUser;

    @BeforeEach
    void setUp() {
        pendingUser = new User();
        pendingUser.setEmail("john@example.com");
        pendingUser.setFullName("John Doe");
        pendingUser.setRole(Role.USER);
        pendingUser.setAccountStatus(AccountStatus.PENDING_VERIFICATION);

        activeUser = new User();
        activeUser.setEmail("active@example.com");
        activeUser.setFullName("Active User");
        activeUser.setRole(Role.USER);
        activeUser.setAccountStatus(AccountStatus.ACTIVE);
    }

    @Nested
    @DisplayName("sendOtp")
    class SendOtpTests {

        @Test
        @DisplayName("Should generate OTP and send verification email for pending user")
        void shouldSendOtpForPendingUser() {
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(pendingUser));
            when(otpProperties.ttl()).thenReturn(600_000L); // 10 minutes
            when(otpService.generate("john@example.com")).thenReturn("123456");

            emailVerificationService.sendOtp("john@example.com");

            verify(otpService).generate("john@example.com");
            verify(emailService).sendHtmlEmail(
                    eq("john@example.com"),
                    eq("Email verification"),
                    eq("email/authentication/verify-email"),
                    any()
            );
        }

        @Test
        @DisplayName("Should throw EMAIL_ALREADY_VERIFIED when user is already active")
        void shouldThrowWhenUserAlreadyActive() {
            when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() -> emailVerificationService.sendOtp("active@example.com"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_VERIFIED));
        }

        @Test
        @DisplayName("Should throw USER_NOT_FOUND when user does not exist")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> emailVerificationService.sendOtp("unknown@example.com"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("resendOtp")
    class ResendOtpTests {

        @Test
        @DisplayName("Should resend OTP for pending user")
        void shouldResendOtpForPendingUser() {
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(pendingUser));
            when(otpProperties.ttl()).thenReturn(600_000L);
            when(otpService.generate("john@example.com")).thenReturn("654321");

            emailVerificationService.resendOtp("john@example.com");

            verify(otpService).generate("john@example.com");
            verify(emailService).sendHtmlEmail(
                    eq("john@example.com"),
                    eq("Email verification"),
                    eq("email/authentication/verify-email"),
                    any()
            );
        }

        @Test
        @DisplayName("Should throw EMAIL_ALREADY_VERIFIED when resending for active user")
        void shouldThrowWhenResendingForActiveUser() {
            when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() -> emailVerificationService.resendOtp("active@example.com"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_VERIFIED));
        }
    }

    @Nested
    @DisplayName("verifyOtp")
    class VerifyOtpTests {

        @Test
        @DisplayName("Should verify OTP and activate user account")
        void shouldVerifyOtpAndActivateAccount() {
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(pendingUser));

            emailVerificationService.verifyOtp("john@example.com", "123456");

            verify(otpService).verify("john@example.com", "123456");
            verify(userRepository).save(pendingUser);
            assertThat(pendingUser.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should throw EMAIL_ALREADY_VERIFIED when verifying active user")
        void shouldThrowWhenVerifyingActiveUser() {
            when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() -> emailVerificationService.verifyOtp("active@example.com", "123456"))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> assertThat(((AuthException) ex).getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_VERIFIED));
        }
    }
}
