package ai.fin.controller;

import ai.fin.dto.budget.BudgetRequest;
import ai.fin.dto.budget.BudgetResponse;
import ai.fin.dto.budget.BudgetStatusResponse;
import ai.fin.security.UserPrincipal;
import ai.fin.service.BudgetService;
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

import java.time.YearMonth;
import java.util.List;

@Tag(name = "Budgets", description = "APIs for managing monthly budgets and tracking spending status")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(
            summary = "Create a new budget",
            description = "Creates a budget for a specific category and month. Ensures uniqueness per category per month."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Budget created successfully",
                    content = @Content(schema = @Schema(implementation = BudgetResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or duplicate budget",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<BudgetResponse>> create(
            @Valid @RequestBody BudgetRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = budgetService.create(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "Budget created successfully",
                response
        );
    }

    @Operation(
            summary = "Get budgets by month",
            description = "Fetches all budgets for the authenticated user for a given month."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Budgets fetched successfully",
                    content = @Content(schema = @Schema(implementation = BudgetResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid month format",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<BudgetResponse>>> getByMonth(
            @Parameter(
                    description = "Month in format YYYY-MM",
                    example = "2026-03",
                    required = true
            )
            @RequestParam YearMonth month,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = budgetService.getByMonth(principal.getId(), month);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Budgets fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Get budget status",
            description = "Returns current budget status including spent amount, remaining amount, and usage percentage."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Budget status fetched successfully",
                    content = @Content(schema = @Schema(implementation = BudgetStatusResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiSuccessResponse<BudgetStatusResponse>> getStatus(
            @Parameter(description = "Unique identifier of the budget", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = budgetService.getStatus(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Budget status fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Delete budget",
            description = "Deletes a budget for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Budget deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
            @Parameter(description = "Unique identifier of the budget", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        budgetService.delete(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                "Budget deleted successfully",
                null
        );
    }
}
