package ai.fin.service;

import ai.fin.dto.chat.*;
import ai.fin.shared.exception.types.ChatSessionException;

import java.util.List;

public interface ChatService {

    /**
     * Retrieves all chat sessions for the current user.
     *
     * @param userId user identifier
     * @return list of chat session responses
     */
    List<ChatSessionResponse> getSessions(String userId);

    /**
     * Creates a new chat session for the current user.
     * If no title is provided, a default title "New Chat" is assigned.
     *
     * @param userId  user identifier
     * @param request session creation request
     * @return created chat session response
     */
    ChatSessionResponse createSession(String userId, CreateSessionRequest request);

    /**
     * Deletes a chat session owned by the current user, and the associated messages
     * are deleted via cascading.
     *
     * @param userId    user identifier
     * @param sessionId session identifier
     * @throws ChatSessionException if a session is not found or not owned by a user
     */
    void deleteSession(String userId, String sessionId);

    /**
     * Retrieves all messages for a given chat session.
     * <p>Messages are ordered chronologically.
     *
     * @param userId    user identifier
     * @param sessionId session identifier
     * @return list of chat message responses
     * @throws ChatSessionException if a session is not found or not owned by the user
     */
    List<ChatMessageResponse> getMessages(String userId, String sessionId);

    /**
     * Processes a user query within a chat session.
     *
     * @param userId    user identifier
     * @param sessionId chat session identifier
     * @param request   query request containing a user question
     * @return response containing a generated answer
     * @throws ChatSessionException if a session is not found or not owned by a user
     */
    ChatQueryResponse query(String userId, String sessionId, ChatQueryRequest request);
}
