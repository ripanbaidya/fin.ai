package ai.fin.controller;

import ai.fin.dto.auth.AuthResponse;
import ai.fin.dto.auth.LoginRequest;
import ai.fin.dto.auth.RefreshTokenRequest;
import ai.fin.dto.auth.ResendOtpRequest;
import ai.fin.dto.auth.SendOtpRequest;
import ai.fin.dto.auth.TokenResponse;
import ai.fin.dto.auth.UserRegisterRequest;
import ai.fin.dto.auth.UserResponse;
import ai.fin.dto.auth.VerifyEmailRequest;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.service.AuthService;
import ai.fin.service.EmailVerificationService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.handler.GlobalExceptionHandler;
import ai.fin.shared.exception.types.AuthException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("POST /auth/register and /auth/signup")
    class RegisterTests {

        @Test
        @DisplayName("Should successfully register user via /auth/register")
        void shouldRegisterUser() throws Exception {
            UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "password123");
            UserResponse userResponse = new UserResponse("user-1", Role.USER.name(), AccountStatus.PENDING_VERIFICATION.name());
            TokenResponse tokenResponse = TokenResponse.of("access-token", "refresh-token", 900000L);
            AuthResponse authResponse = new AuthResponse(userResponse, tokenResponse);

            when(authService.registerUser(any(UserRegisterRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.code").value("CREATED"))
                    .andExpect(jsonPath("$.message").value("User Registered Successfully"))
                    .andExpect(jsonPath("$.data.user.id").value("user-1"));
        }

        @Test
        @DisplayName("Should support backwards compatibility via /auth/signup")
        void shouldRegisterUserViaSignupAlias() throws Exception {
            UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "password123");
            UserResponse userResponse = new UserResponse("user-1", Role.USER.name(), AccountStatus.PENDING_VERIFICATION.name());
            TokenResponse tokenResponse = TokenResponse.of("access-token", "refresh-token", 900000L);
            AuthResponse authResponse = new AuthResponse(userResponse, tokenResponse);

            when(authService.registerUser(any(UserRegisterRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.data.user.id").value("user-1"));
        }

        @Test
        @DisplayName("Should return 400 when registration request is invalid")
        void shouldReturnBadRequestWhenRegistrationInvalid() throws Exception {
            UserRegisterRequest request = new UserRegisterRequest("", "invalid-email", "short");

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("Should successfully authenticate user")
        void shouldLoginUser() throws Exception {
            LoginRequest request = new LoginRequest("john@example.com", "password123");
            UserResponse userResponse = new UserResponse("user-1", Role.USER.name(), AccountStatus.ACTIVE.name());
            TokenResponse tokenResponse = TokenResponse.of("access-token", "refresh-token", 900000L);
            AuthResponse authResponse = new AuthResponse(userResponse, tokenResponse);

            when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.code").value("OK"))
                    .andExpect(jsonPath("$.message").value("Login Successful"))
                    .andExpect(jsonPath("$.data.user.id").value("user-1"));
        }
    }

    @Nested
    @DisplayName("POST /auth/refresh-token and /auth/refresh")
    class RefreshTokenTests {

        @Test
        @DisplayName("Should successfully refresh token via /auth/refresh-token")
        void shouldRefreshToken() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
            TokenResponse tokenResponse = TokenResponse.of("new-access-token", "new-refresh-token", 900000L);

            when(authService.refreshToken(eq("valid-refresh-token"))).thenReturn(tokenResponse);

            mockMvc.perform(post("/auth/refresh-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
        }

        @Test
        @DisplayName("Should successfully refresh token via /auth/refresh alias")
        void shouldRefreshTokenViaAlias() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
            TokenResponse tokenResponse = TokenResponse.of("new-access-token", "new-refresh-token", 900000L);

            when(authService.refreshToken(eq("valid-refresh-token"))).thenReturn(tokenResponse);

            mockMvc.perform(post("/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
        }
    }

    @Nested
    @DisplayName("POST /auth/send-otp and /auth/send-verification")
    class SendOtpTests {

        @Test
        @DisplayName("Should successfully send OTP via /auth/send-otp")
        void shouldSendOtp() throws Exception {
            SendOtpRequest request = new SendOtpRequest("john@example.com");
            doNothing().when(emailVerificationService).sendOtp("john@example.com");

            mockMvc.perform(post("/auth/send-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("OTP sent successfully."));

            verify(emailVerificationService).sendOtp("john@example.com");
        }

        @Test
        @DisplayName("Should successfully send OTP via /auth/send-verification alias")
        void shouldSendOtpViaAlias() throws Exception {
            SendOtpRequest request = new SendOtpRequest("john@example.com");
            doNothing().when(emailVerificationService).sendOtp("john@example.com");

            mockMvc.perform(post("/auth/send-verification")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200));

            verify(emailVerificationService).sendOtp("john@example.com");
        }
    }

    @Nested
    @DisplayName("POST /auth/resend-otp and /auth/resend-verification")
    class ResendOtpTests {

        @Test
        @DisplayName("Should successfully resend OTP via /auth/resend-otp")
        void shouldResendOtp() throws Exception {
            ResendOtpRequest request = new ResendOtpRequest("john@example.com");
            doNothing().when(emailVerificationService).resendOtp("john@example.com");

            mockMvc.perform(post("/auth/resend-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("Verification OTP resent successfully."));

            verify(emailVerificationService).resendOtp("john@example.com");
        }

        @Test
        @DisplayName("Should successfully resend OTP via /auth/resend-verification alias")
        void shouldResendOtpViaAlias() throws Exception {
            ResendOtpRequest request = new ResendOtpRequest("john@example.com");
            doNothing().when(emailVerificationService).resendOtp("john@example.com");

            mockMvc.perform(post("/auth/resend-verification")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200));

            verify(emailVerificationService).resendOtp("john@example.com");
        }

        @Test
        @DisplayName("Should return 409 when email is already verified")
        void shouldReturnConflictWhenAlreadyVerified() throws Exception {
            ResendOtpRequest request = new ResendOtpRequest("john@example.com");
            doThrow(new AuthException(ErrorCode.EMAIL_ALREADY_VERIFIED))
                    .when(emailVerificationService).resendOtp("john@example.com");

            mockMvc.perform(post("/auth/resend-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_VERIFIED"));
        }
    }

    @Nested
    @DisplayName("POST /auth/verify-otp and /auth/verify-email")
    class VerifyOtpTests {

        @Test
        @DisplayName("Should successfully verify email via /auth/verify-otp")
        void shouldVerifyEmail() throws Exception {
            VerifyEmailRequest request = new VerifyEmailRequest("john@example.com", "123456");
            doNothing().when(emailVerificationService).verifyOtp("john@example.com", "123456");

            mockMvc.perform(post("/auth/verify-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("Email verified successfully. You can now log in."));

            verify(emailVerificationService).verifyOtp("john@example.com", "123456");
        }

        @Test
        @DisplayName("Should successfully verify email via /auth/verify-email alias")
        void shouldVerifyEmailViaAlias() throws Exception {
            VerifyEmailRequest request = new VerifyEmailRequest("john@example.com", "123456");
            doNothing().when(emailVerificationService).verifyOtp("john@example.com", "123456");

            mockMvc.perform(post("/auth/verify-email")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200));

            verify(emailVerificationService).verifyOtp("john@example.com", "123456");
        }
    }
}
