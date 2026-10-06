package ai.fin.controller;

import ai.fin.dto.chat.*;
import ai.fin.security.UserPrincipal;
import ai.fin.service.ChatService;
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

@Tag(name = "Chat", description = "APIs for AI-assisted financial chat, session management, and querying")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class ChatController {

    private final ChatService chatService;

    @Operation(
            summary = "Get all chat sessions",
            description = "Fetches all conversation sessions belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Chat sessions fetched successfully",
                    content = @Content(schema = @Schema(implementation = ChatSessionResponse.class))
            )
    })
    @GetMapping("/sessions")
    public ResponseEntity<ApiSuccessResponse<List<ChatSessionResponse>>> getSessions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = chatService.getSessions(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Chat sessions fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Create a new chat session",
            description = "Creates a new chat session for conversation management."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Chat session created successfully",
                    content = @Content(schema = @Schema(implementation = ChatSessionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/sessions")
    public ResponseEntity<ApiSuccessResponse<ChatSessionResponse>> createSession(
            @Valid @RequestBody(required = false) CreateSessionRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CreateSessionRequest effectiveRequest = request != null ? request : new CreateSessionRequest(null);
        var response = chatService.createSession(principal.getId(), effectiveRequest);
        return ApiResponseFactory.success(
                HttpStatus.CREATED,
                SuccessCode.CREATED.getCode(),
                "Chat session created successfully",
                response
        );
    }

    @Operation(
            summary = "Delete a chat session",
            description = "Deletes a chat session and all its associated messages."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Chat session deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Chat session not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteSession(
            @Parameter(description = "Unique identifier of the chat session", required = true)
            @PathVariable String sessionId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        chatService.deleteSession(principal.getId(), sessionId);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                "Chat session deleted successfully",
                null
        );
    }

    @Operation(
            summary = "Get messages for a chat session",
            description = "Retrieves chronological message history for a specific chat session."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Messages fetched successfully",
                    content = @Content(schema = @Schema(implementation = ChatMessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Chat session not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiSuccessResponse<List<ChatMessageResponse>>> getMessages(
            @Parameter(description = "Unique identifier of the chat session", required = true)
            @PathVariable String sessionId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = chatService.getMessages(principal.getId(), sessionId);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Chat messages fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Query financial assistant within a chat session",
            description = "Processes a user question using RAG and persists the conversation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Query processed successfully",
                    content = @Content(schema = @Schema(implementation = ChatQueryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid question payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Chat session not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/sessions/{sessionId}/query")
    public ResponseEntity<ApiSuccessResponse<ChatQueryResponse>> query(
            @Parameter(description = "Unique identifier of the chat session", required = true)
            @PathVariable String sessionId,
            @Valid @RequestBody ChatQueryRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var response = chatService.query(principal.getId(), sessionId, request);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Query processed successfully",
                response
        );
    }
}
