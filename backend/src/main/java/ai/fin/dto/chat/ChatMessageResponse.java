package ai.fin.dto.chat;

import ai.fin.enums.MessageRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        name = "ChatMessageResponse",
        description = "Represents a chat message in a conversation session"
)
public record ChatMessageResponse(

        @Schema(description = "Unique identifier of the chat message")
        String id,

        @Schema(
                description = "Role of the message sender",
                example = "USER",
                allowableValues = {"USER", "ASSISTANT"}
        )
        MessageRole role,

        @Schema(
                description = "Content of the chat message",
                example = "Show my expenses for this month."
        )
        String content,

        @Schema(
                description = "Timestamp when the message was created",
                example = "2026-03-16T12:30:45Z"
        )
        Instant createdAt

) {
}