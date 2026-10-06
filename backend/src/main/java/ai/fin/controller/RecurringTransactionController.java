package ai.fin.controller;

import ai.fin.dto.recurring.ForecastSummaryResponse;
import ai.fin.dto.recurring.RecurringTransactionRequest;
import ai.fin.dto.recurring.RecurringTransactionResponse;
import ai.fin.dto.recurring.UpdateRecurringTransactionRequest;
import ai.fin.security.UserPrincipal;
import ai.fin.service.RecurringTransactionService;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recurring Transactions", description = "API's for managing recurring transactions")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/recurring-transactions")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class RecurringTransactionController {

    private final RecurringTransactionService recurringService;

    @Operation(
            summary = "Create a recurring transaction",
            description = "Creates a recurring transaction rule with a defined schedule and amount."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Recurring transaction created successfully",
                    content = @Content(schema = @Schema(implementation = RecurringTransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload or date range",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<RecurringTransactionResponse>> create(
            @Valid @RequestBody RecurringTransactionRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = recurringService.create(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "Recurring transaction created successfully",
                response
        );
    }

    @Operation(
            summary = "Get all recurring transactions",
            description = "Fetches all active recurring transactions for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurring transactions fetched successfully",
                    content = @Content(schema = @Schema(implementation = RecurringTransactionResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<RecurringTransactionResponse>>> getAllByUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = recurringService.getAllByUser(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Recurring transactions fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Get recurring transaction by ID",
            description = "Fetches a specific recurring transaction using its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurring transaction fetched successfully",
                    content = @Content(schema = @Schema(implementation = RecurringTransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurring transaction not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<RecurringTransactionResponse>> getById(
            @Parameter(description = "Unique identifier of the recurring transaction", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = recurringService.getById(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Recurring transaction fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Update a recurring transaction",
            description = "Updates an existing recurring transaction. Supports partial updates and validates date range."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurring transaction updated successfully",
                    content = @Content(schema = @Schema(implementation = RecurringTransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurring transaction not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<RecurringTransactionResponse>> update(

            @Parameter(description = "Unique identifier of the recurring transaction", required = true)
            @PathVariable String id,

            @Valid @RequestBody UpdateRecurringTransactionRequest request,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = recurringService.update(principal.getId(), id, request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.UPDATED.getCode(),
                "Recurring transaction updated successfully",
                response
        );
    }

    @Operation(
            summary = "Deactivate a recurring transaction",
            description = "Soft deletes (deactivates) a recurring transaction so it no longer generates future transactions."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurring transaction deactivated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurring transaction not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> deactivate(

            @Parameter(description = "Unique identifier of the recurring transaction", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        recurringService.deactivate(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.UPDATED.getCode(),
                "Recurring transaction deactivated successfully",
                null
        );
    }

    @Operation(
            summary = "Get forecast",
            description = "Generates projected income, expenses, and net balance based on active recurring transactions."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Forecast generated successfully",
                    content = @Content(schema = @Schema(implementation = ForecastSummaryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid number of days",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/forecast")
    public ResponseEntity<ApiSuccessResponse<ForecastSummaryResponse>> forecast(
            @Parameter(description = "Number of days for forecast (1 to 365)", example = "30")
            @RequestParam(defaultValue = "30")
            @Min(1) @Max(365) int days,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = recurringService.forecast(principal.getId(), days);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Forecast generated successfully",
                response
        );
    }
}
