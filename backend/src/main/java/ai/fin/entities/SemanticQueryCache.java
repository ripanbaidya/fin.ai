package ai.fin.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Stores cached LLM responses keyed by the query embedding vector.
 * Used for semantic query caching to avoid redundant LLM calls for semantically
 * equivalent finance queries.
 */
@Entity
@Table(name = "semantic_query_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SemanticQueryCache extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "query_text", nullable = false, columnDefinition = "TEXT")
    private String queryText;

    @Column(name = "cached_answer", nullable = false, columnDefinition = "TEXT")
    private String cachedAnswer;

    @Column(name = "similarity_hits", nullable = false)
    private int similarityHits;

    @Column(name = "ttl_expires_at", nullable = false)
    private Instant ttlExpiresAt;
}