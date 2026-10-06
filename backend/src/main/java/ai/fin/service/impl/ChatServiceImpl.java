package ai.fin.service.impl;

import ai.fin.dto.chat.*;
import ai.fin.entities.ChatMessage;
import ai.fin.entities.ChatSession;
import ai.fin.entities.User;
import ai.fin.enums.MessageRole;
import ai.fin.mapper.ChatMapper;
import ai.fin.rag.RagQueryService;
import ai.fin.repository.ChatMessageRepository;
import ai.fin.repository.ChatSessionRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.ChatService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.ChatSessionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final RagQueryService ragQueryService;

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getSessions(String userId) {
        return chatSessionRepository.findByUser_IdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(ChatMapper::toSessionResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChatSessionResponse createSession(String userId, CreateSessionRequest request) {
        User user = currentUser(userId);

        ChatSession session = new ChatSession();
        session.setUser(user);
        session.setTitle(normalizeTitle(request.title()));

        ChatSession createdSession = chatSessionRepository.saveAndFlush(session);

        return ChatMapper.toSessionResponse(createdSession);
    }

    @Override
    @Transactional
    public void deleteSession(String userId, String sessionId) {
        ChatSession session = getOwnedSession(userId, sessionId);
        chatSessionRepository.delete(session); // Cascades to messages
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(String userId, String sessionId) {
        ChatSession session = getOwnedSession(userId, sessionId);
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId())
                .stream()
                .map(ChatMapper::toMessageResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChatQueryResponse query(String userId, String sessionId, ChatQueryRequest request) {
        User user = currentUser(userId);

        ChatSession session = getOwnedSession(userId, sessionId);

        // Fetch existing conversation history BEFORE saving the new user message
        // so the current question is not included in the history passed to the LLM
        List<ChatMessage> history = chatMessageRepository
                .findBySessionIdOrderByCreatedAtAsc(session.getId());

        // Persist user message
        saveMessage(session, MessageRole.USER, request.question());

        // RAG query
        String answer = ragQueryService.query(user.getId(), request.question(), history);

        // Persist assistant reply
        saveMessage(session, MessageRole.ASSISTANT, answer);

        // Update session metadata
        session.setTitle(deriveTitle(session, request.question()));
        chatSessionRepository.save(session);

        return new ChatQueryResponse(answer);
    }

    private User currentUser(String userId) {
        return userRepository.getReferenceById(userId);
    }

    private ChatSession getOwnedSession(String userId, String sessionId) {
        return chatSessionRepository.findByIdAndUser_Id(sessionId, userId)
                .orElseThrow(() ->
                        new ChatSessionException(ErrorCode.CHAT_SESSION_NOT_FOUND));
    }

    private void saveMessage(ChatSession session, MessageRole role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setSession(session);
        msg.setRole(role);
        msg.setContent(content);
        chatMessageRepository.save(msg);
    }

    private String normalizeTitle(String title) {
        if (title == null) return "New Chat";

        String trimmed = title.trim();
        if (trimmed.isEmpty() || "string".equalsIgnoreCase(trimmed)) {
            return "New Chat";
        }
        return trimmed;
    }

    private String deriveTitle(ChatSession session, String question) {
        if (!"New Chat".equals(session.getTitle())) return session.getTitle();

        return question.length() > 60 ? question.substring(0, 57) + "..." : question;
    }
}
