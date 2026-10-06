package ai.fin.controller;

import ai.fin.dto.auth.DeleteAccountRequest;
import ai.fin.dto.auth.LogoutRequest;
import ai.fin.dto.user.UpdateProfileRequest;
import ai.fin.dto.user.UserProfileDetails;
import ai.fin.security.UserPrincipal;
import ai.fin.service.AuthService;
import ai.fin.service.UserService;
import ai.fin.shared.api.ApiErrorResponse;
import ai.fin.shared.api.ApiSuccessResponse;
import ai.fin.shared.api.SuccessCode;
import ai.fin.shared.api.factory.ApiResponseFactory;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.AuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "User Account", description = "Authenticated account management APIs: session logout and account deletion")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Missing or invalid Bearer access token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(
            summary = "Log out the current session",
            description = "Blacklists the current access token in Redis and revokes the supplied refresh token from the database. " +
                    "Only ends this session — other active sessions for the user remain unaffected."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Session logged out successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or malformed request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account or refresh token not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiSuccessResponse<Void>> logout(
            @Parameter(description = "Bearer access token (format: 'Bearer <token>')", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody LogoutRequest request
    ) {
        String accessToken = Optional.ofNullable(authHeader)
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring(7))
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_AUTH_HEADER));
        authService.logout(accessToken, request.refreshToken());
        return ApiResponseFactory.success(
                HttpStatus.NO_CONTENT,
                SuccessCode.OK.getCode(),
                "Logout Successful",
                null
        );
    }

    @Operation(
            summary = "Request account deletion",
            description = "Confirms identity via password, marks the account DELETED, and starts a " +
                    "configurable grace period before permanent removal. All active sessions are revoked " +
                    "immediately; the current access token is blacklisted so it cannot be reused for its " +
                    "remaining lifetime."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account deletion requested; grace period started",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or malformed request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Password does not match, or invalid access token",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account already pending deletion",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Account has already been deleted",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/me")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteAccount(
            @Parameter(description = "Bearer access token (format: 'Bearer <token>')", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody DeleteAccountRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String accessToken = Optional.ofNullable(authHeader)
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring(7))
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_AUTH_HEADER));

        authService.deleteAccount(principal.getId(), request.password(), accessToken);

        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                "Account deletion requested. Your account will be permanently removed after the notice period.",
                null
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiSuccessResponse<UserProfileDetails>> getProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = userService.getProfile(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Account details retrieved successfully",
                response
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiSuccessResponse<UserProfileDetails>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = userService.updateProfile(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.UPDATED.getCode(),
                "Account details updated successfully",
                response
        );
    }
}