package ai.fin.controller;

import ai.fin.dto.user.UserProfileDetails;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.security.UserPrincipal;
import ai.fin.service.UserService;
import ai.fin.shared.api.ApiErrorResponse;
import ai.fin.shared.api.ApiSuccessResponse;
import ai.fin.shared.api.PaginatedData;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "Admin specific APIs for managing users and system settings")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Missing or invalid Bearer access token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Forbidden - Admin access required",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class AdminController {

    private final UserService userService;

    @Operation(
            summary = "Get all users (paginated)",
            description = "Retrieves a paginated list of all users. Accessible only to ADMIN users."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Users fetched successfully",
            content = @Content(schema = @Schema(implementation = UserProfileDetails.class))
    )
    @GetMapping("/users")
    public ResponseEntity<ApiSuccessResponse<PaginatedData<UserProfileDetails>>> getAllUsers(
            @Parameter(description = "Zero-based page index")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size (1-100)")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,

            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));
        PaginatedData<UserProfileDetails> response = userService.getAllUsers(principal.getId(), pageable);

        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Users fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Get user by ID",
            description = "Fetches the profile of a specific user using their unique ID. Accessible only to ADMIN users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User fetched successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileDetails.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiSuccessResponse<UserProfileDetails>> getProfileById(
            @Parameter(description = "User ID to fetch profile for", required = true)
            @PathVariable String userId,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = userService.getProfile(userId);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "User profile fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Get user count by filters",
            description = "Returns total number of users filtered by role and active status. Accessible only to ADMIN users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User count fetched successfully",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/users/count")
    public ResponseEntity<ApiSuccessResponse<Long>> getUserCount(
            @Parameter(description = "Role to filter users (e.g., USER, ADMIN)")
            @RequestParam Role role,

            @Parameter(description = "Filter by account status")
            @RequestParam AccountStatus accountStatus,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long response = userService.countTotalUsers(principal.getId(), role, accountStatus);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "User Count Fetched Successfully",
                response
        );
    }
}
