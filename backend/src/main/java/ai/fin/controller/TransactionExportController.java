package ai.fin.controller;

import ai.fin.security.UserPrincipal;
import ai.fin.service.TransactionExportService;
import ai.fin.shared.api.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Tag(name = "Transactions export", description = "APIs for exporting user's transactions data")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/transactions/export")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class TransactionExportController {

    private static final MediaType CSV_MEDIA_TYPE = new MediaType("text", "csv", StandardCharsets.UTF_8);

    private final TransactionExportService transactionExportService;

    @Operation(
            summary = "Export transactions as CSV",
            description = "Downloads all transactions for the authenticated user as a UTF-8 encoded CSV file."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CSV file downloaded successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed to generate export file",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/csv")
    public ResponseEntity<StreamingResponseBody> exportCsv(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        StreamingResponseBody responseBody =
                outputStream -> transactionExportService.exportAsCsv(principal.getId(), outputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, attachmentHeader())
                .contentType(CSV_MEDIA_TYPE)
                .body(responseBody);
    }

    private String attachmentHeader() {
        return ContentDisposition.attachment()
                .filename("transactions-" + LocalDate.now() + ".csv")
                .build()
                .toString();
    }
}
