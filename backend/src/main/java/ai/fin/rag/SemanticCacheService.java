package ai.fin.rag;

import ai.fin.repository.SemanticQueryCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SemanticCacheService {

    private final SemanticQueryCacheRepository cacheRepository;

    @Transactional(readOnly = true)
    public Object[] findBestCacheHit(String userId, String queryEmbedding, double threshold, Instant now) {
        return cacheRepository.findBestCacheHit(userId, queryEmbedding, threshold, now);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementHitCount(String cacheId) {
        cacheRepository.incrementHitCount(cacheId);
    }

    @Transactional
    public void evictUserCache(String userId) {
        if (userId != null) {
            cacheRepository.deleteByUserId(userId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void storeInCache(
            String userId,
            String question,
            String embedding,
            String answer,
            Instant expiresAt
    ) {
        Instant now = Instant.now();
        cacheRepository.insertWithEmbedding(
                UUID.randomUUID().toString(),
                now,
                now,
                userId,
                question,
                embedding,
                answer,
                0,
                expiresAt
        );
    }
}
