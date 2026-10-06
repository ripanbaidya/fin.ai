package ai.fin.service;

import ai.fin.dto.recurring.ForecastSummaryResponse;
import ai.fin.dto.recurring.RecurringTransactionRequest;
import ai.fin.dto.recurring.RecurringTransactionResponse;
import ai.fin.dto.recurring.UpdateRecurringTransactionRequest;
import ai.fin.shared.exception.types.RecurringTransactionException;

import java.util.List;

public interface RecurringTransactionService {

    /**
     * Creates a new recurring transaction for a user.
     * Validates the date range and initializes the next execution date based on the start date.
     *
     * @param userId  user identifier
     * @param request recurring transaction request
     * @return created recurring transaction response
     * @throws RecurringTransactionException if the date range is invalid
     */
    RecurringTransactionResponse create(String userId, RecurringTransactionRequest request);

    /**
     * Retrieves all active recurring transactions for a user.
     *
     * @param userId user identifier
     * @return list of recurring transaction responses
     */
    List<RecurringTransactionResponse> getAllByUser(String userId);

    /**
     * Retrieves a recurring transaction by ID for a user.
     *
     * @param userId         user identifier
     * @param recurringTxnId recurring transaction identifier
     * @return recurring transaction response
     * @throws RecurringTransactionException if not found or access is denied
     */
    RecurringTransactionResponse getById(String userId, String recurringTxnId);

    /**
     * Updates an existing recurring transaction.
     * Supports partial updates. Validates date range when the end date is modified.
     *
     * @param userId         user identifier
     * @param recurringTxnId recurring transaction identifier
     * @param request        update request
     * @return updated recurring transaction response
     * @throws RecurringTransactionException if not found or access is denied
     */
    RecurringTransactionResponse update(String userId, String recurringTxnId, UpdateRecurringTransactionRequest request);

    /**
     * Deactivates a recurring transaction.
     * Once deactivated, it will no longer generate future transactions.
     *
     * @param userId         user identifier
     * @param recurringTxnId recurring transaction identifier
     * @throws RecurringTransactionException if not found, access is denied, or already inactive
     */
    void deactivate(String userId, String recurringTxnId);

    // todo - add delete method in future

    /**
     * Generates a financial forecast based on recurring transactions.
     * Calculates projected income, expenses, and net balance over the specified number of days.
     * Only considers active recurring transactions within the given window.
     *
     * @param userId user identifier
     * @param days   number of days to forecast
     * @return forecast summary response including projected entries and totals
     */
    ForecastSummaryResponse forecast(String userId, int days);

    /*
     * Processes all due recurring transactions for the current date.
     * For each due recurring transaction:
     * - Creates a corresponding transaction
     * - Updates the next execution date
     * - Deactivates the recurring if the end date is reached
     * Failures are logged and do not interrupt the processing of other records.
     */
    void processDueRecurringTransactions();
}
