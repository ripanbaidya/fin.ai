package ai.fin.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "ChatQueryRequest",
        description = "Request payload containing a user's question for the chat assistant"
)
public record ChatQueryRequest(

        @Schema(
                description = "User's question or query sent to the assistant",
                example = "How much did I spend on food this month?",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Question must not be blank")
        @Size(max = 1000, message = "Question must not exceed 1000 characters")
        String question

) {
}