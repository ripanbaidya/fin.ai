package ai.fin.rag.service;

import ai.fin.entities.Transaction;

public interface EmbeddingService {

    /**
     * Converts the transaction to text, generates an embedding, and stores it in the vector store.
     *
     * @param transaction the transaction to store
     * @return the vector document ID (embeddingId), or null if unavailable
     */
    String store(Transaction transaction);

    /**
     * Deletes the old vector and stores a fresh one for the updated transaction.
     *
     * @param embeddingId the vector document ID (embeddingId)
     * @param transaction the updated transaction
     * @return the new vector document ID (embeddingId), or null if unavailable
     */
    String update(String embeddingId, Transaction transaction);

    /**
     * Deletes the vector document from the vector store.
     * Safe to call with a null embeddingId — will do nothing.
     *
     * @param embeddingId the vector document ID (embeddingId)
     */
    void delete(String embeddingId);
}