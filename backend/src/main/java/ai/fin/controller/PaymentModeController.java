package ai.fin.controller;

import ai.fin.dto.paymentmode.CreatePaymentModeRequest;
import ai.fin.dto.paymentmode.PaymentModeResponse;
import ai.fin.dto.paymentmode.UpdatePaymentModeRequest;
import ai.fin.security.UserPrincipal;
import ai.fin.service.PaymentModeService;
import ai.fin.shared.api.ApiErrorResponse;
import ai.fin.shared.api.ApiSuccessResponse;
import ai.fin.shared.api.SuccessCode;
import ai.fin.shared.api.factory.ApiResponseFactory;
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

import java.util.List;

@Tag(name = "Payment Modes", description = "APIs for managing transaction payment modes (e.g., Cash, Credit Card, UPI)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/payment-modes")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class PaymentModeController {

    private final PaymentModeService paymentModeService;

    @Operation(
            summary = "Get all payment modes",
            description = "Retrieves all payment modes visible to the authenticated user, including system defaults and user-created payment modes."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment modes retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<PaymentModeResponse>>> getAll(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<PaymentModeResponse> paymentModes = paymentModeService.getAll(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Payment modes retrieved successfully",
                paymentModes
        );
    }

    @Operation(
            summary = "Create a new payment mode",
            description = "Creates a new custom payment mode for the authenticated user. Payment mode names must be unique per user (case-insensitive)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment mode created successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A payment mode with the same name already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<PaymentModeResponse>> create(
            @Valid @RequestBody CreatePaymentModeRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PaymentModeResponse paymentMode = paymentModeService.create(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "Payment mode created successfully",
                paymentMode
        );
    }

    @Operation(
            summary = "Update an existing payment mode",
            description = "Updates an existing user-owned payment mode. System default payment modes cannot be modified."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment mode updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - cannot modify system default payment mode or payment mode belonging to another user",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment mode not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A payment mode with the updated name already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<PaymentModeResponse>> update(
            @Parameter(description = "Unique identifier of the payment mode to update", required = true)
            @PathVariable("id") String id,
            @Valid @RequestBody UpdatePaymentModeRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PaymentModeResponse paymentMode = paymentModeService.update(principal.getId(), id, request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.UPDATED.getCode(),
                "Payment mode updated successfully",
                paymentMode
        );
    }

    @Operation(
            summary = "Delete a payment mode",
            description = "Deletes an existing user-owned payment mode. System default payment modes cannot be deleted."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment mode deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - cannot delete system default payment mode or payment mode belonging to another user",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment mode not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
            @Parameter(description = "Unique identifier of the payment mode to delete", required = true)
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        paymentModeService.delete(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                "Payment mode deleted successfully",
                null
        );
    }
}
