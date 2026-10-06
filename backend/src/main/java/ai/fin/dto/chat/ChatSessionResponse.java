package ai.fin.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        name = "ChatSessionResponse",
        description = "Represents a chat session belonging to a user"
)
public record ChatSessionResponse(

        @Schema(description = "Unique identifier of the chat session")
        String id,

        @Schema(
                description = "Title of the chat session",
                example = "Monthly Expense Analysis"
        )
        String title,

        @Schema(
                description = "Timestamp when the chat session was created",
                example = "2026-03-16T10:15:30Z"
        )
        Instant createdAt,

        @Schema(
                description = "Timestamp when the chat session was last updated",
                example = "2026-03-16T10:20:45Z"
        )
        Instant updatedAt

) {
}