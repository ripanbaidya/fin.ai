package ai.fin.controller;

import ai.fin.dto.notification.NotificationResponse;
import ai.fin.dto.notification.UnreadNotificationCountResponse;
import ai.fin.enums.NotificationType;
import ai.fin.security.UserPrincipal;
import ai.fin.service.NotificationService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notifications", description = "APIs for managing user notifications")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Get user notifications",
            description = "Fetches paginated notifications for the authenticated user with optional filtering by read status or type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications fetched successfully"
            )
    })
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<PaginatedData<NotificationResponse>>> getAll(
            @Parameter(description = "Filter by unread status only")
            @RequestParam(required = false) Boolean unreadOnly,

            @Parameter(description = "Filter by notification type")
            @RequestParam(required = false) NotificationType type,

            @Parameter(description = "Zero-based page index")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size (1-100)")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,

            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));
        PaginatedData<NotificationResponse> response = notificationService.getNotifications(
                principal.getId(), unreadOnly, type, pageable);

        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Notifications fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Get unread notifications count",
            description = "Returns the total number of unread notifications for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Unread notification count fetched successfully"
            )
    })
    @GetMapping("/unread-count")
    public ResponseEntity<ApiSuccessResponse<UnreadNotificationCountResponse>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UnreadNotificationCountResponse response = notificationService.getUnreadCount(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Unread count fetched successfully",
                response
        );
    }

    @Operation(
            summary = "Mark notification as read",
            description = "Marks a specific notification as read by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification marked as read successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiSuccessResponse<NotificationResponse>> markAsRead(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        NotificationResponse response = notificationService.markAsRead(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "Notification marked as read",
                response
        );
    }

    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications as read for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "All notifications marked as read successfully"
            )
    })
    @PatchMapping("/read-all")
    public ResponseEntity<ApiSuccessResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        notificationService.markAllAsRead(principal.getId());
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.OK.getCode(),
                "All notifications marked as read",
                null
        );
    }

    @Operation(
            summary = "Delete notification",
            description = "Deletes a specific notification by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable String id,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        notificationService.deleteNotification(principal.getId(), id);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                "Notification deleted successfully",
                null
        );
    }

    @Operation(
            summary = "Delete all notifications",
            description = "Deletes all notifications (or only read notifications if readOnly is true) for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications deleted successfully"
            )
    })
    @DeleteMapping
    public ResponseEntity<ApiSuccessResponse<Void>> deleteAll(
            @Parameter(description = "If true, only deletes read notifications. If false, deletes all.")
            @RequestParam(defaultValue = "false") boolean readOnly,

            @AuthenticationPrincipal UserPrincipal principal
    ) {
        notificationService.deleteAllNotifications(principal.getId(), readOnly);
        return ApiResponseFactory.success(
                HttpStatus.OK,
                SuccessCode.DELETED.getCode(),
                readOnly ? "Read notifications deleted successfully" : "All notifications deleted successfully",
                null
        );
    }
}
