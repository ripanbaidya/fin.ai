package ai.fin.rag.service.impl;

import ai.fin.entities.Transaction;
import ai.fin.rag.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Primary // Default implementation
@RequiredArgsConstructor
public class PgVectorEmbeddingServiceImpl implements EmbeddingService {

    private final VectorStore vectorStore;

    @Override
    public String store(Transaction transaction) {
        try {
            Document document = toDocument(transaction);
            vectorStore.add(List.of(document));
            log.debug("Document added to vector store: {}, transactionId: {}", document, transaction.getId());
            return document.getId();
        } catch (Exception e) {
            log.error("Error storing document: {}", transaction.getId(), e);
            return null;
        }
    }

    /**
     * Converts a transaction to a Document for embedding.
     *
     * @param txn the transaction to convert
     * @return a Document representing the transaction
     */
    private Document toDocument(Transaction txn) {
        String category = txn.getCategory() != null ? txn.getCategory().getName() : "Uncategorized";
        String paymentMode = txn.getPaymentMode() != null ? txn.getPaymentMode().getName() : "Unknown";
        String note = txn.getNote() != null ? txn.getNote() : "No note";

        String content = """
                Transaction ID: %s
                Date: %s
                Type: %s
                Amount: %.2f
                Category: %s
                Payment Mode: %s
                Note: %s
                """
                .formatted(txn.getId(), txn.getDate(), txn.getType(), txn.getAmount(), category, paymentMode, note);

        Map<String, Object> metadata = buildMetadata(txn);
        return new Document(content, metadata);
    }

    /**
     * Build metadata for embedding
     */
    private Map<String, Object> buildMetadata(Transaction txn) {
        return Map.of(
                "transactionId", txn.getId(),
                "userId", txn.getUser().getId(),
                "type", txn.getType().name(),
                "date", txn.getDate().toString(),
                "amount", txn.getAmount()
        );
    }

    @Override
    public String update(String embeddingId, Transaction transaction) {
        delete(embeddingId);
        return store(transaction);
    }

    @Override
    public void delete(String embeddingId) {
        if (embeddingId == null || embeddingId.isBlank()) {
            return;
        }

        try {
            vectorStore.delete(List.of(embeddingId));
            log.debug("Deleted embedding id={}", embeddingId);
        } catch (Exception e) {
            log.error("Failed to delete embedding id={}", embeddingId, e);
        }
    }
}
