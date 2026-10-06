package ai.fin.rag.service.impl;

import ai.fin.entities.Transaction;
import ai.fin.rag.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoOpEmbeddingServiceImpl implements EmbeddingService {

    @Override
    public String store(Transaction transaction) {
        log.debug("NoOp embedding store called for transaction id: {}", transaction.getId());
        return "";
    }

    @Override
    public String update(String embeddingId, Transaction transaction) {
        log.debug("NoOp embedding update called for transaction id={}", transaction.getId());
        return "";
    }

    @Override
    public void delete(String embeddingId) {
        log.debug("NoOp embedding delete called for embeddingId={}", embeddingId);
    }
}
