package ai.fin.controller;

import ai.fin.dto.transaction.CreateTransactionRequest;
import ai.fin.dto.transaction.TransactionFilterRequest;
import ai.fin.dto.transaction.TransactionResponse;
import ai.fin.dto.transaction.UpdateTransactionRequest;
import ai.fin.enums.TransactionSortField;
import ai.fin.enums.TxnType;
import ai.fin.security.UserPrincipal;
import ai.fin.service.TransactionService;
import ai.fin.shared.api.ApiErrorResponse;
import ai.fin.shared.api.ApiSuccessResponse;
import ai.fin.shared.api.PaginatedData;
import ai.fin.shared.api.SuccessCode;
import ai.fin.shared.api.factory.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Transactions", description = "API's for managing transaction's related operations")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Get all transactions",
            description = "Fetches transactions with optional filters such as type, category, and date range. Supports pagination and sorting."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions fetched successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
    })
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<PaginatedData<TransactionResponse>>> getAll(
            @Parameter(
                    description = "Transaction type filter (INCOME or EXPENSE)",
                    examples = {
                            @ExampleObject("INCOME"),
                            @ExampleObject("EXPENSE")
                    }
            )
            @RequestParam(required = false) TxnType type,

            @Parameter(description = "Category ID to filter transactions")
            @RequestParam(required = false) String categoryId,

            @Parameter(description = "Start date (inclusive) in ISO format yyyy-MM-dd")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateFrom,

            @Parameter(description = "End date (inclusive) in ISO format yyyy-MM-dd")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateTo,

            @Parameter(description = "Zero-based page index")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size (1-100)")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "DATE") TransactionSortField sortBy,

            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("Date from must be before date to");
        }

        var filterRequest = TransactionFilterRequest.from(type, categoryId, dateFrom, dateTo);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy.getProperty()));

        Page<TransactionResponse> responses = transactionService
                .getAll(principal.getId(), filterRequest, pageable);

        return ApiResponseFactory.successPage(
                SuccessCode.OK.getCode(),
                "Transactions fetched successfully",
                responses
        );
    }

    @Operation(
            summary = "Get transaction by ID",
            description = "Fetches a specific transaction using its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction fetched successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<TransactionResponse>> getById(
            @Parameter(description = "Unique identifier of the transaction", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = transactionService.getById(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Transaction fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Create a transaction",
            description = "Creates a new transaction (INCOME or EXPENSE). Validates category and payment mode."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
    })
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<TransactionResponse>> create(
            @Valid @RequestBody CreateTransactionRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = transactionService.create(principal.getId(), request);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "Transaction Created Successfully",
                response
        );
    }

    @Operation(
            summary = "Update a transaction",
            description = "Updates an existing transaction. Supports partial updates and validates ownership."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction updated successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<TransactionResponse>> update(
            @Parameter(description = "Unique identifier of the transaction", required = true)
            @PathVariable String id,

            @Valid @RequestBody UpdateTransactionRequest request,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = transactionService.update(principal.getId(), id, request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.UPDATED.getCode(),
                "Transaction Updated Successfully",
                response
        );
    }

    @Operation(
            summary = "Delete a transaction",
            description = "Deletes a transaction using its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
            @Parameter(description = "Unique identifier of the transaction", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        transactionService.delete(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                "Transaction deleted successfully",
                null
        );
    }
}
