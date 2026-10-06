package ai.fin.controller;

import ai.fin.dto.category.CategoryResponse;
import ai.fin.dto.category.CreateCategoryRequest;
import ai.fin.dto.category.UpdateCategoryRequest;
import ai.fin.enums.CategoryType;
import ai.fin.security.UserPrincipal;
import ai.fin.service.CategoryService;
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

@Tag(name = "Categories", description = "APIs for managing transaction categories")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Retrieves all categories visible to the authenticated user, including system defaults and user-created categories. Optionally filter by category type (INCOME or EXPENSE)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category type parameter",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<CategoryResponse>>> getAll(
            @Parameter(description = "Optional filter by category type (INCOME or EXPENSE)")
            @RequestParam(name = "type", required = false) CategoryType type,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<CategoryResponse> categories = categoryService.getAll(principal.getId(), type);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Categories retrieved successfully",
                categories
        );
    }

    @Operation(
            summary = "Create a new category",
            description = "Creates a new custom category for the authenticated user. Category names must be unique per user and type (case-insensitive)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A category with the same name and type already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<CategoryResponse>> create(
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CategoryResponse category = categoryService.create(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "Category created successfully",
                category
        );
    }

    @Operation(
            summary = "Update an existing category",
            description = "Updates an existing user-owned category. System default categories cannot be modified."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - cannot modify system default category or category belonging to another user",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A category with the updated name and type already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<CategoryResponse>> update(
            @Parameter(description = "Unique identifier of the category to update", required = true)
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateCategoryRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CategoryResponse category = categoryService.update(principal.getId(), id, request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.UPDATED.getCode(),
                "Category updated successfully",
                category
        );
    }

    @Operation(
            summary = "Delete a category",
            description = "Deletes an existing user-owned category. System default categories cannot be deleted."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - cannot delete system default category or category belonging to another user",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
            @Parameter(description = "Unique identifier of the category to delete", required = true)
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        categoryService.delete(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                "Category deleted successfully",
                null
        );
    }
}
