package ai.fin.controller;

import ai.fin.dto.savingsgoal.ContributeRequest;
import ai.fin.dto.savingsgoal.GoalProgressResponse;
import ai.fin.dto.savingsgoal.SavingsGoalRequest;
import ai.fin.security.UserPrincipal;
import ai.fin.service.SavingsGoalService;
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

@Tag(name = "Savings Goals", description = "APIs for managing user savings goals")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/savings-goals")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @Operation(
            summary = "Create a new savings goal",
            description = "Creates a new savings goal with a target amount and deadline. Initializes with zero savings and IN_PROGRESS status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Goal created successfully",
                    content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<GoalProgressResponse>> create(
            @Valid @RequestBody SavingsGoalRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = savingsGoalService.create(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "Savings goal created successfully",
                response
        );
    }

    @Operation(
            summary = "Get all savings goals",
            description = "Fetches all savings goals belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Goals fetched successfully",
                    content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<GoalProgressResponse>>> getAll(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = savingsGoalService.getAll(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Savings goals fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Contribute to a savings goal",
            description = "Adds a contribution to a savings goal. Marks the goal as ACHIEVED if target amount is reached."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contribution added successfully",
                    content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid contribution amount or goal already completed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping("/{id}/contribute")
    public ResponseEntity<ApiSuccessResponse<GoalProgressResponse>> contribute(
            @Parameter(description = "Unique identifier of the savings goal", required = true)
            @PathVariable String id,

            @Parameter(description = "Contribution request payload", required = true)
            @Valid @RequestBody ContributeRequest request,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = savingsGoalService.contribute(principal.getId(), id, request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.UPDATED.getCode(),
                "Contribution added successfully",
                response
        );
    }

    @Operation(
            summary = "Get savings goal progress",
            description = "Returns detailed progress including saved amount, remaining amount, and completion status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Goal progress fetched successfully",
                    content = @Content(schema = @Schema(implementation = GoalProgressResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{id}/progress")
    public ResponseEntity<ApiSuccessResponse<GoalProgressResponse>> getProgress(
            @Parameter(description = "Unique identifier of the savings goal", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = savingsGoalService.getProgress(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Goal progress fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Delete savings goal",
            description = "Deletes a savings goal for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
            @Parameter(description = "Unique identifier of the savings goal", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        savingsGoalService.delete(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                "Savings Goal deleted successfully",
                null
        );
    }
}
