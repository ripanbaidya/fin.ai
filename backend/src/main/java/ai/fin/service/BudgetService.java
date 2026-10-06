package ai.fin.service;

import ai.fin.dto.budget.BudgetRequest;
import ai.fin.dto.budget.BudgetResponse;
import ai.fin.dto.budget.BudgetStatusResponse;
import ai.fin.shared.exception.types.BudgetException;
import ai.fin.shared.exception.types.CategoryException;

import java.time.YearMonth;
import java.util.List;

public interface BudgetService {

    /**
     * Creates a new budget for the current user.
     * Ensures that only one budget exists per category per month.
     *
     * @throws BudgetException   if a budget already exists for the same category and month
     * @throws CategoryException if the category does not exist
     */
    BudgetResponse create(String userId, BudgetRequest request);

    /**
     * Retrieves all budgets for the current user for a given month.
     */
    List<BudgetResponse> getByMonth(String userId, YearMonth month);

    /**
     * Retrieves the current status of a budget.
     * Calculates total spent amount, remaining budget, usage percentage and determines
     * whether alert or limit thresholds are breached.
     *
     * @throws BudgetException if the budget is not found or access is denied
     */
    BudgetStatusResponse getStatus(String userId, String budgetId);

    /**
     * Deletes a budget owned by the current user.
     *
     * @throws BudgetException if a budget is not found or access is denied
     */
    void delete(String userId, String budgetId);
}
