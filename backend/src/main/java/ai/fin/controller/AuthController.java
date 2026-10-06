package ai.fin.controller;

import ai.fin.dto.auth.*;
import ai.fin.service.AuthService;
import ai.fin.service.EmailVerificationService;
import ai.fin.shared.api.ApiErrorResponse;
import ai.fin.shared.api.ApiSuccessResponse;
import ai.fin.shared.api.SuccessCode;
import ai.fin.shared.api.factory.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Public authentication APIs: user registration, credential login, token refresh, and email OTP verification")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "Register a new user account",
            description = "Creates a new user account with PENDING_VERIFICATION status and issues an initial JWT access and refresh token pair."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or malformed request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "An account with this email address already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<ApiSuccessResponse<AuthResponse>> register(
            @Valid @RequestBody UserRegisterRequest request
    ) {
        var response = authService.registerUser(request);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "User Registered Successfully",
                response
        );
    }

    @Operation(
            summary = "Authenticate user and issue tokens",
            description = "Validates user credentials (email and password) and returns user profile details with JWT access and refresh tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful, tokens issued",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or malformed request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Account has been deleted or marked for deletion",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        var response = authService.login(request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Login Successful",
                response
        );
    }

    @Operation(
            summary = "Refresh access token",
            description = "Validates a refresh token, revokes it to prevent token reuse (rotation), and issues a fresh pair of access and refresh tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or malformed request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token is invalid, expired, revoked, or not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Account has been deleted or marked for deletion",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(value = {"/refresh-token", "/refresh"})
    public ResponseEntity<ApiSuccessResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        var response = authService.refreshToken(request.refreshToken());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Token Refreshed Successfully",
                response
        );
    }

    @Operation(
            summary = "Send email verification OTP",
            description = "Generates and sends a time-based one-time password (OTP) to the registered user's email address for account verification."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification OTP sent successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or malformed request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email is already verified",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Account has been deleted or marked for deletion",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Cooldown active - please wait before requesting another OTP",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed to send verification email",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(value = {"/send-otp", "/send-verification"})
    public ResponseEntity<ApiSuccessResponse<Void>> sendOtp(
            @Valid @RequestBody SendOtpRequest request
    ) {
        emailVerificationService.sendOtp(request.email());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "OTP sent successfully.",
                null
        );
    }

    @Operation(
            summary = "Resend email verification OTP",
            description = "Resends a verification OTP to the user's email address if the account is still pending verification and cooldown period has elapsed."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification OTP resent successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or malformed request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email is already verified",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Account has been deleted or marked for deletion",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Cooldown active - please wait before requesting another OTP",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed to send verification email",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(value = {"/resend-otp", "/resend-verification"})
    public ResponseEntity<ApiSuccessResponse<Void>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request
    ) {
        emailVerificationService.resendOtp(request.email());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Verification OTP resent successfully.",
                null
        );
    }

    @Operation(
            summary = "Verify email with OTP",
            description = "Verifies the submitted one-time password (OTP) and transitions the account status from PENDING_VERIFICATION to ACTIVE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully, account activated",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "OTP is invalid or expired, or validation failed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email is already verified",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Account has been deleted or marked for deletion",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Maximum OTP verification attempts exceeded",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(value = {"/verify-otp", "/verify-email"})
    public ResponseEntity<ApiSuccessResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        emailVerificationService.verifyOtp(request.email(), request.otp());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Email verified successfully. You can now log in.",
                null
        );
    }
}