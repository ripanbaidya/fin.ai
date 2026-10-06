package ai.fin.controller;

import ai.fin.dto.dashboard.DashboardResponse;
import ai.fin.security.UserPrincipal;
import ai.fin.service.DashboardService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@Tag(name = "Dashboard", description = "APIs for fetching user financial dashboard insights")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
})
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Get dashboard data",
            description = "Returns aggregated financial insights including income, expenses, category breakdown, trends, and top expenses for a given month."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard fetched successfully",
                    content = @Content(schema = @Schema(implementation = DashboardResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid month format (expected yyyy-MM)",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<DashboardResponse>> getDashboard(
            @Parameter(description = "Month in format yyyy-MM (defaults to current month if not provided)", example = "2026-03")
            @RequestParam(required = false) String month,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        YearMonth yearMonth = (month != null) ? YearMonth.parse(month) : YearMonth.now();
        var response = dashboardService.getDashboard(principal.getId(), yearMonth);

        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Dashboard information fetched successfully",
                response
        );
    }
}