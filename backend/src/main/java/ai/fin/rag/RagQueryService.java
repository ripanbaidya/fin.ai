package ai.fin.rag;

import ai.fin.entities.ChatMessage;
import ai.fin.enums.MessageRole;
import ai.fin.service.ContextService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.RAGQueryException;
import ai.fin.shared.exception.types.VectorSearchException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagQueryService {

    /**
     * The path to the prompt file containing the prompt template for the query.
     */
    private static final String PROMPT_PATH = "prompts/query-prompt.txt";

    /**
     * The maximum number of top results to be considered during the query processing.
     * This constant defines the number of best matches or candidates that will be
     * retrieved based on their relevance to the query.
     */
    private static final int TOP_K = 8;

    /**
     * The maximum number of historical queries to be considered during the query processing.
     */
    private static final int MAX_HISTORY = 10;

    // Semantic cache configuration.
    private static final double CACHE_SIMILARITY_THRESHOLD = 0.92;
    private static final long CACHE_TTL_HOURS = 6;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

    private final ContextService contextService;
    private final SemanticCacheService semanticCacheService;

    private String queryPrompt;

    @PostConstruct
    private void loadPrompt() {
        try {
            ClassPathResource resource = new ClassPathResource(PROMPT_PATH);
            try (var inputStream = resource.getInputStream()) {
                this.queryPrompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            if (queryPrompt.isBlank()) {
                throw new IllegalStateException("System prompt file is empty: " + PROMPT_PATH);
            }

            log.info("WalletIQ system prompt loaded ({} chars) from {}", queryPrompt.length(), PROMPT_PATH);
        } catch (IOException e) {
            throw new IllegalStateException(ErrorCode.RAG_PROMPT_LOAD_FAILED.getDefaultMessage(), e);
        }
    }

    public String query(String userId, String question, List<ChatMessage> history) {
        String normalizedQuestion = question == null ? "" : question.trim();
        if (normalizedQuestion.isBlank()) return fallbackAnswer();

        // Generate the query embedding once and reused for both cache lookup and vector search
        float[] queryEmbedding = embeddingModel.embed(normalizedQuestion);

        // Check semantic cache for the best match
        String cachedAnswer = lookupCache(userId, queryEmbedding);
        if (cachedAnswer != null) {
            log.info("Semantic cache hit for userId={}", userId);
            return sanitizeMarkdown(cachedAnswer);
        }

        log.debug("Semantic cache miss for userId={}, proceeding with full RAG pipeline", userId);

        // Retrieve relevant transactions for this user only
        List<Document> context = retrieve(normalizedQuestion, userId);

        // Add context for Budget & goals
        String budgetContext = contextService.buildBudgetContext(userId);
        String goalContext = contextService.buildSavingsGoalContext(userId);

        // Build combined context block
        String transactionContext = context.isEmpty()
                ? "I couldn't find any relevant transactions to answer that question."
                : context.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        String fullContext = """
                %s
                
                %s
                
                === RELEVANT TRANSACTIONS ===
                %s
                """.formatted(budgetContext, goalContext, transactionContext);

        // Call LLM
        if (context.isEmpty() && isEmptyContext(budgetContext, goalContext)) {
            return fallbackAnswer();
        }

        String rawResponse = callLlm(fullContext, normalizedQuestion, userId, history);

        // Store the result in semantic cache
        storeInCache(userId, normalizedQuestion, queryEmbedding, rawResponse);

        // Sanitize the response
        return sanitizeMarkdown(rawResponse);
    }

    private void storeInCache(String userId, String question, float[] queryEmbedding, String answer) {
        try {
            String embeddingStr = embeddingToString(queryEmbedding);
            semanticCacheService.storeInCache(
                    userId,
                    question,
                    embeddingStr,
                    answer,
                    Instant.now().plus(CACHE_TTL_HOURS, ChronoUnit.HOURS)
            );

            log.debug("Cached RAG response for userId={}", userId);
        } catch (Exception e) {
            log.warn("Failed to store semantic cache entry, continuing without cache", e);
        }
    }

    private List<Document> retrieve(String question, String userId) {
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(TOP_K)
                .filterExpression("userId == '" + userId + "'")
                .build();

        try {
            return vectorStore.similaritySearch(request);
        } catch (Exception e) {
            log.error("Vector search failed for userId={}", userId, e);
            throw new VectorSearchException(ErrorCode.VECTOR_SEARCH_FAILED,
                    "Vector search failed for userId=" + userId
            );
        }
    }
    private String sanitizeMarkdown(String raw) {
        if (raw == null || raw.isBlank()) {
            return fallbackAnswer();
        }

        return raw
                // Fix literal escape sequences from some LLMs
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\r", "")

                // Strip code block wrappers if LLM wraps anyway
                .replaceAll("(?s)^```(?:markdown|text)?\\s*", "")
                .replaceAll("(?s)```\\s*$", "")

                // Strip preamble the LLM sneaks in despite instructions
                .replaceAll("(?i)^(based on (the |your )?(data|context|transactions)[,.]?\\s*)", "")
                .replaceAll("(?i)^(certainly[!,.]?\\s*|sure[!,.]?\\s*|of course[!,.]?\\s*)", "")
                .replaceAll("(?i)^(here (is|are) (your|the|a)[^\n]*\n)", "")

                // Normalize line endings and excess whitespace
                .replaceAll("\r\n|\r", "\n")
                .replaceAll("[ \t]+\n", "\n")
                .replaceAll("\n{3,}", "\n\n")

                .trim();
    }

    /**
     * Looks up the semantic cache for a similar query from the same user.
     * Returns the cached answer if similarity >= threshold, otherwise null.
     */
    private String lookupCache(String userId, float[] queryEmbedding) {
        try {
            String embeddingStr = embeddingToString(queryEmbedding);
            Object[] row = semanticCacheService.findBestCacheHit(
                    userId, embeddingStr, CACHE_SIMILARITY_THRESHOLD, Instant.now()
            );

            if (row != null && row.length >= 3) {
                String cacheId = row[0].toString();
                String cachedAnswer = (String) row[1];
                double similarity = ((Number) row[2]).doubleValue();

                log.debug("Cache hit: similarity={} for userId={}", similarity, userId);
                semanticCacheService.incrementHitCount(cacheId);
                return cachedAnswer;
            }
        } catch (Exception e) {
            log.warn("Semantic cache lookup failed, proceeding without cache", e);
        }

        return null;
    }

    private String embeddingToString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(embedding[i]);
        }
        sb.append("]");
        return sb.toString();
    }


    private String callLlm(String contextBlock, String question, String userId,
                           List<ChatMessage> history) {
        try {
            List<ChatMessage> recentHistory = history.size() > MAX_HISTORY
                    ? history.subList(history.size() - MAX_HISTORY, history.size())
                    : history;

            // Build the conversation history block for context
            String historyBlock = buildHistoryBlock(recentHistory);

            return chatClient.prompt()
                    .system(queryPrompt)
                    .user("""
                            %s
                            Context (your transactions):
                            %s
                            
                            Question: %s
                            """.formatted(historyBlock, contextBlock, question))
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("LLM call failed for userId: {} question: {}", userId, question, e);
            throw new RAGQueryException(ErrorCode.RAG_QUERY_FAILED, "LLM call failed for userId=" + userId);
        }
    }

    private boolean isEmptyContext(String budgetContext, String goalContext) {
        String b = budgetContext == null ? "" : budgetContext.toLowerCase();
        String g = goalContext == null ? "" : goalContext.toLowerCase();
        boolean noBudgets = b.isBlank() || b.contains("no budgets found") || b.contains("no budgets set");
        boolean noGoals = g.isBlank() || g.contains("no savings goals set") || g.contains("no savings goals found");
        return noBudgets && noGoals;
    }


    /**
     * Formats past conversation messages into a readable block that the LLM can
     * use as context for the current question.
     *
     * @param history the list of previous messages to format
     * @return formatted history block or empty string if there is no history
     */
    private String buildHistoryBlock(List<ChatMessage> history) {
        if (history == null || history.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== CONVERSATION HISTORY ===\n");

        for (ChatMessage message : history) {
            String role = message.getRole() == MessageRole.USER
                    ? MessageRole.USER.name() : MessageRole.ASSISTANT.name();
            sb.append(role).append(": ").append(message.getContent()).append("\n");
        }
        sb.append("=== END OF HISTORY ===\n\n");
        return sb.toString();
    }

    private String fallbackAnswer() {
        return "I'm not able to answer that question from your finance data.";
    }
}
