package ai.fin.service;

import ai.fin.dto.transaction.CreateTransactionRequest;
import ai.fin.dto.transaction.TransactionFilterRequest;
import ai.fin.dto.transaction.TransactionResponse;
import ai.fin.dto.transaction.UpdateTransactionRequest;
import ai.fin.shared.exception.types.CategoryException;
import ai.fin.shared.exception.types.PaymentModeException;
import ai.fin.shared.exception.types.TransactionException;
import ai.fin.shared.exception.types.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    /**
     * Retrieves transactions for the current user using optional filters.
     *
     * @param userId   user identifier
     * @param filter   filtering criteria (type, category, date range)
     * @param pageable pagination information
     * @return paginated list of transactions
     */
    Page<TransactionResponse> getAll(String userId, TransactionFilterRequest filter, Pageable pageable);

    /**
     * Retrieves a transaction by ID for the current user.
     *
     * @param userId        user identifier
     * @param transactionId transaction identifier
     * @return transaction details
     * @throws UserException        if the current user is not found
     * @throws TransactionException if the transaction is not found
     *                              or does not belong to the user
     */
    TransactionResponse getById(String userId, String transactionId);

    /**
     * Creates a new transaction for the current user.
     * <p>Category and payment mode must be valid and accessible to the user.
     * Also triggers budget checks and embedding.
     *
     * @param userId  user identifier
     * @param request transaction creation request
     * @return created transaction response
     * @throws CategoryException    if a category is not found or not accessible
     * @throws PaymentModeException if a payment mode is not found or not accessible
     * @throws UserException        if the current user is not found
     */
    TransactionResponse create(String userId, CreateTransactionRequest request);

    /**
     * Updates an existing transaction belonging to the current user.
     * <p>Supports partial updates. Also updates embedding and triggers budget checks if applicable.
     *
     * @param userId        user identifier
     * @param transactionId transaction identifier
     * @param request       update request
     * @return updated transaction response
     * @throws TransactionException transaction is not found or not owned by the user,
     *                              or if the category type does not match the transaction type
     * @throws CategoryException    if a category is invalid or not accessible
     * @throws PaymentModeException if the payment mode is invalid or not accessible
     * @throws UserException        if the current user is not found
     */
    TransactionResponse update(String userId, String transactionId, UpdateTransactionRequest request);

    /**
     * Deletes a transaction belonging to the current user.
     * And also removes the associated embedding.
     *
     * @param userId        user identifier
     * @param transactionId transaction identifier
     * @throws TransactionException if a transaction is not found or not owned by the user
     * @throws UserException        if the current user is not found
     */
    void delete(String userId, String transactionId);

}