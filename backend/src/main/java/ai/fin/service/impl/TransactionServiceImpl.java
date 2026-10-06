package ai.fin.service.impl;

import ai.fin.dto.transaction.CreateTransactionRequest;
import ai.fin.dto.transaction.TransactionFilterRequest;
import ai.fin.dto.transaction.TransactionResponse;
import ai.fin.dto.transaction.UpdateTransactionRequest;
import ai.fin.entities.Category;
import ai.fin.entities.PaymentMode;
import ai.fin.entities.Transaction;
import ai.fin.entities.User;
import ai.fin.enums.CategoryType;
import ai.fin.enums.TxnType;
import ai.fin.mapper.TransactionMapper;
import ai.fin.rag.SemanticCacheService;
import ai.fin.rag.service.EmbeddingService;
import ai.fin.repository.CategoryRepository;
import ai.fin.repository.PaymentModeRepository;
import ai.fin.repository.TransactionRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.BudgetAlertService;
import ai.fin.service.TransactionService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.CategoryException;
import ai.fin.shared.exception.types.PaymentModeException;
import ai.fin.shared.exception.types.TransactionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");

    private final EmbeddingService embeddingService;
    private final BudgetAlertService budgetAlertService;
    private final SemanticCacheService semanticCacheService;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentModeRepository paymentModeRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAll(String userId, TransactionFilterRequest filter, Pageable pageable) {
        LocalDate dateFrom = filter.dateFrom();
        LocalDate dateTo = filter.dateTo();

        if (dateFrom == null && dateTo == null) {
            YearMonth currentMonth = YearMonth.now();
            dateFrom = currentMonth.atDay(1);
            dateTo = currentMonth.atEndOfMonth();
        }

        Page<Transaction> page = transactionRepository
                .findAllByFilter(currentUser(userId), filter.type(), filter.categoryId(), dateFrom, dateTo, pageable);

        return page.map(TransactionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getById(String userId, String transactionId) {
        return transactionRepository.findByIdAndUser_Id(transactionId, userId)
                .map(TransactionMapper::toResponse)
                .orElseThrow(() -> new TransactionException(
                        ErrorCode.TRANSACTION_NOT_FOUND, "Transaction not found or does not belong to the user")
                );

    }

    @Override
    @Transactional
    public TransactionResponse create(String userId, CreateTransactionRequest request) {
        User user = currentUser(userId);

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDate(request.date());
        transaction.setNote(request.note());
        transaction.setUser(user);

        var categoryType = getCategoryType(request.type());

        transaction.setCategory(resolveCategory(request.categoryId(), userId, categoryType));
        transaction.setPaymentMode(resolvePaymentMode(request.paymentModeId(), userId));

        transactionRepository.saveAndFlush(transaction);

        // Check for budget alert
        if (transaction.getType() == TxnType.EXPENSE && transaction.getCategory() != null) {
            budgetAlertService.checkAndAlert(userId, transaction.getCategory().getId(), YearMonth.from(transaction.getDate()));
        }

        String embedId = embeddingService.store(transaction);
        if (embedId != null) {
            transaction.setEmbeddingId(embedId);
            transactionRepository.save(transaction);
        }

        // Invalidate semantic query cache since financial data changed
        evictSemanticCache(userId);

        return TransactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse update(String userId, String transactionId, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository
                .findByIdAndUser_Id(transactionId, userId)
                .orElseThrow(() -> new TransactionException(
                        ErrorCode.TRANSACTION_NOT_FOUND, "Transaction not found or does not belong to the user")
                );

        // Take the effective type from the request or use the existing one if not specified
        TxnType effectiveType = request.type() != null ? request.type() : transaction.getType();
        Category category = transaction.getCategory();
        PaymentMode paymentMode = transaction.getPaymentMode();

        // Resolve category and payment mode if specified
        if (request.categoryId() != null) {
            category = resolveCategory(request.categoryId(), userId, getCategoryType(effectiveType));
        }
        if (request.paymentModeId() != null) {
            paymentMode = resolvePaymentMode(request.paymentModeId(), userId);
        }
        // Check if category type matches transaction type
        if (category != null && category.getCategoryType() != getCategoryType(effectiveType)) {
            throw new TransactionException(
                    ErrorCode.INVALID_CATEGORY_TYPE, "Category type does not match transaction type"
            );
        }

        // Update transaction fields
        if (request.amount() != null) transaction.setAmount(request.amount());
        if (request.type() != null) transaction.setType(request.type());
        if (request.date() != null) transaction.setDate(request.date());
        if (request.note() != null) transaction.setNote(request.note());

        transaction.setCategory(category);
        transaction.setPaymentMode(paymentMode);

        // Make sure to flush it, so the sum query in checkAndAlert sees the updated amount, category, date
        transactionRepository.flush();

        // Check for budget alerts
        if (effectiveType == TxnType.EXPENSE && transaction.getCategory() != null) {
            budgetAlertService.checkAndAlert(userId, transaction.getCategory().getId(), YearMonth.from(transaction.getDate()));
        }

        // Update embedding if present
        String embedId = embeddingService.update(transaction.getEmbeddingId(), transaction);
        if (embedId != null) {
            transaction.setEmbeddingId(embedId);
            transactionRepository.save(transaction);
        }

        // Invalidate semantic query cache since financial data changed
        evictSemanticCache(userId);

        return TransactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public void delete(String userId, String transactionId) {
        Transaction transaction = transactionRepository
                .findByIdAndUser_Id(transactionId, userId)
                .orElseThrow(() -> new TransactionException(
                        ErrorCode.TRANSACTION_NOT_FOUND, "Transaction not found or does not belong to the user")
                );

        // Delete embedding from the vector store when relevant transaction is deleted
        embeddingService.delete(transaction.getEmbeddingId());
        transactionRepository.delete(transaction);

        // Invalidate semantic query cache since financial data changed
        evictSemanticCache(userId);
    }

    /**
     * Evicts all semantic query cache entries for the given user.
     * Called when financial data changes to ensure stale cached answers are not served.
     *
     * @param userId The ID of the user whose cache entries should be evicted.
     */
    private void evictSemanticCache(String userId) {
        if (userId != null && semanticCacheService != null) {
            try {
                semanticCacheService.evictUserCache(userId);
            } catch (Exception e) {
                log.warn("Failed to evict semantic cache for userId: {}", userId, e);
            }
        }
    }

    // Helpers

    private User currentUser(String userId) {
        return userRepository.getReferenceById(userId);
    }

    /**
     * Maps a transaction type to a category type.
     */
    private CategoryType getCategoryType(TxnType txnType) {
        return txnType == TxnType.INCOME ? CategoryType.INCOME : CategoryType.EXPENSE;
    }

    /**
     * Resolves a category accessible to the user.
     */
    private Category resolveCategory(String categoryId, String userId, CategoryType categoryType) {
        return categoryRepository
                .findAllVisibleToUser(userId, categoryType).stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new CategoryException(
                        ErrorCode.CATEGORY_NOT_FOUND, "Category not found or not accessible")
                );
    }

    /**
     * Resolves a payment mode accessible to the user.
     */
    private PaymentMode resolvePaymentMode(String paymentModeId, String userId) {
        return paymentModeRepository
                .findAllVisibleToUser(userId).stream()
                .filter(p -> p.getId().equals(paymentModeId))
                .findFirst()
                .orElseThrow(() -> new PaymentModeException(
                        ErrorCode.PAYMENT_MODE_NOT_FOUND, "Payment mode not found or not accessible")
                );
    }
}
