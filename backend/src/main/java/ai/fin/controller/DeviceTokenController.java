package ai.fin.controller;

import ai.fin.dto.devices.RegisterDeviceTokenRequest;
import ai.fin.dto.devices.UnregisterDeviceTokenRequest;
import ai.fin.security.UserPrincipal;
import ai.fin.service.DeviceTokenService;
import ai.fin.shared.api.ApiErrorResponse;
import ai.fin.shared.api.ApiSuccessResponse;
import ai.fin.shared.api.SuccessCode;
import ai.fin.shared.api.factory.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Device Tokens", description = "APIs for managing device tokens for push notifications")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/devices/tokens")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @Operation(
            summary = "Register device token",
            description = "Registers or updates a push notification device token for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device token registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<Void>> register(
            @Valid @RequestBody RegisterDeviceTokenRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        deviceTokenService.register(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Device token registered successfully",
                null
        );
    }

    @Operation(
            summary = "Unregister device token",
            description = "Removes a push notification device token for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device token unregistered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping
    public ResponseEntity<ApiSuccessResponse<Void>> unregister(
            @Valid @RequestBody UnregisterDeviceTokenRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        deviceTokenService.unregister(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Device token unregistered successfully",
                null
        );
    }
}
