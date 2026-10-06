package ai.fin.mapper;

import ai.fin.dto.chat.ChatMessageResponse;
import ai.fin.dto.chat.ChatSessionResponse;
import ai.fin.entities.ChatMessage;
import ai.fin.entities.ChatSession;

public final class ChatMapper {

    private ChatMapper() {
    }

    public static ChatSessionResponse toSessionResponse(ChatSession session) {
        return new ChatSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    public static ChatMessageResponse toMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}