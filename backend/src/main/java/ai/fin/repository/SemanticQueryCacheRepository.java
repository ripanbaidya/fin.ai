package ai.fin.repository;

import ai.fin.entities.SemanticQueryCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface SemanticQueryCacheRepository extends JpaRepository<SemanticQueryCache, String> {

    /**
     * Finds the most similar cached query for a given user using cosine similarity.
     * Returns the cached answer and similarity score if above the threshold.
     * Only considers non-expired entries.
     *
     * @param userId         the user's ID
     * @param queryEmbedding the embedding vector as a string (e.g., "[0.1,0.2,...]")
     * @param threshold      minimum cosine similarity (e.g., 0.92)
     * @param now            current timestamp for TTL filtering
     * @return Object[] with {id, cached_answer, similarity} or null
     */
    @Query(value = """
            SELECT c.id, c.cached_answer,
                   1 - (c.query_embedding <=> CAST(:queryEmbedding AS vector)) AS similarity
            FROM semantic_query_cache c
            WHERE c.user_id = :userId
              AND c.ttl_expires_at > :now
              AND 1 - (c.query_embedding <=> CAST(:queryEmbedding AS vector)) >= :threshold
            ORDER BY similarity DESC
            LIMIT 1
            """, nativeQuery = true)
    Object[] findBestCacheHit(@Param("userId") String userId, @Param("queryEmbedding") String queryEmbedding,
                              @Param("threshold") double threshold, @Param("now") Instant now);

    /**
     * Increments the similarity_hits counter for a cache entry.
     */
    @Modifying
    @Query("UPDATE SemanticQueryCache c SET c.similarityHits = c.similarityHits + 1 WHERE c.id = :id")
    void incrementHitCount(@Param("id") String id);

    /**
     * Deletes all semantic cache entries for a specific user.
     * Called when user's financial data changes (create/update/delete transaction).
     */
    @Modifying
    @Query("DELETE FROM SemanticQueryCache c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") String userId);

    /**
     * Deletes all expired cache entries across all users.
     *
     * @param now current timestamp
     * @return number of deleted entries
     */
    @Modifying
    @Query("DELETE FROM SemanticQueryCache c WHERE c.ttlExpiresAt <= :now")
    int deleteExpiredEntries(@Param("now") Instant now);

    /**
     * Inserts a semantic cache entry with embedding in a single SQL statement.
     * This avoids NOT NULL constraint failures on query_embedding.
     */
    @Modifying
    @Query(value = """
            INSERT INTO semantic_query_cache
                (id, created_at, updated_at, user_id, query_text, query_embedding, cached_answer, similarity_hits, ttl_expires_at)
            VALUES
                (:id, :createdAt, :updatedAt, :userId, :queryText, CAST(:embedding AS vector), :cachedAnswer, :similarityHits, :ttlExpiresAt)
            """, nativeQuery = true)
    void insertWithEmbedding(
            @Param("id") String id,
            @Param("createdAt") Instant createdAt,
            @Param("updatedAt") Instant updatedAt,
            @Param("userId") String userId,
            @Param("queryText") String queryText,
            @Param("embedding") String embedding,
            @Param("cachedAnswer") String cachedAnswer,
            @Param("similarityHits") int similarityHits,
            @Param("ttlExpiresAt") Instant ttlExpiresAt
    );
}
